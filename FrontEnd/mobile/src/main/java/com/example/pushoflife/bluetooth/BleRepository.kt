package com.example.pushoflife.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.pushoflife.MyApplication
import com.example.pushoflife.utils.Event
import java.nio.ByteBuffer
import java.util.*

class BleRepository {

    private val TAG = "BleRepository"
    private val context = MyApplication.applicationContext()

    private val deviceManager = BleDeviceManager(context)
    private var bleGatt: BluetoothGatt? = null
    private val connectedDevices = mutableSetOf<String>()
    private val deviceQueue: LinkedList<BluetoothDevice> = LinkedList()
    private var bluetoothGattCallback: BluetoothGattCallback? = null
    private val connectionRetryMap = mutableMapOf<String, Int>() // 장치별 재연결 횟수 저장

    val isScanning = MutableLiveData(Event(false))
    val listUpdate = MutableLiveData<Event<List<BluetoothDevice>?>>(Event(emptyList()))
    private val bluetoothLeScanner: BluetoothLeScanner? =
        BluetoothAdapter.getDefaultAdapter()?.bluetoothLeScanner

    init {
        Log.d(TAG, "사용 중인 CONTEXT : $context")
    }

    @SuppressLint("MissingPermission")
    fun startScan(onComplete: () -> Unit) {
        Log.d(TAG, "REPOSITORY에서 스캔 시작")
        deviceManager.clearDeviceList() // 리스트 초기화

        if (!isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)) {
            handleMissingPermission(Manifest.permission.BLUETOOTH_SCAN)
            return
        }

        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString(SERVICE_UUID)))
                .build()
        )
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        bluetoothLeScanner?.startScan(filters, settings, BLEScanCallback)
        isScanning.postValue(Event(true))

        Handler(Looper.getMainLooper()).postDelayed({
            stopScan()
            onComplete()
        }, 5000)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        if (!isPermissionGranted(Manifest.permission.BLUETOOTH_SCAN)) {
            handleMissingPermission(Manifest.permission.BLUETOOTH_SCAN)
            return
        }
        bluetoothLeScanner?.stopScan(BLEScanCallback)
        isScanning.postValue(Event(false))
    }

    private val BLEScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val serviceUuid = UUID.fromString(SERVICE_UUID)
            val scanRecord = result.scanRecord
            val matches = scanRecord?.serviceUuids?.any { it.uuid == serviceUuid } ?: false
            if (matches) {
                deviceManager.addDevice(result.device)
                listUpdate.postValue(Event(deviceManager.getDeviceList()))
                Log.d(TAG, "UUID 일치 기기 발견! LIST에 추가")
            } else {
                Log.d(TAG, "UUID 불일치 기기 필터링")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun sendLocationToAllDevices(latitude: Double, longitude: Double) {
        Log.d(TAG, "LIST에 담긴 기기들에게 메세지 전송")
        val locationData = ByteBuffer.allocate(16).apply {
            putDouble(latitude)
            putDouble(longitude)
        }.array()
        deviceQueue.clear()
        deviceQueue.addAll(deviceManager.getDeviceList())
        connectNextDevice(locationData)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectNextDevice(locationData: ByteArray) {
        val device = deviceQueue.poll() ?: return
        if (connectedDevices.contains(device.address)) {
            Log.d(TAG, "이미 연결 중인 기기: ${device.address}")
            connectNextDevice(locationData)
            return
        }

        if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
            handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
            connectNextDevice(locationData)
            return
        }

        connectedDevices.add(device.address)
        bluetoothGattCallback = createBluetoothGattCallback(locationData)
        bleGatt = connectGattCompat(bluetoothGattCallback!!, device, false)
        connectionRetryMap[device.address] = 0 // 재연결 횟수 초기화
    }

    private fun createBluetoothGattCallback(locationData: ByteArray): BluetoothGattCallback {
        return object : BluetoothGattCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                    handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    return
                }

                Log.d(TAG, "연결 상태 변경: ${gatt.device.address}, status: $status, newState: $newState")

                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "GATT 연결 성공: ${gatt.device.address}")
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED || status == 133) {
                    Log.d(TAG, "GATT 연결 해제 또는 실패: ${gatt.device.address}")
                    cleanupGattConnection(gatt)
                    val retries = connectionRetryMap[gatt.device.address] ?: 0
                    if (retries < 7) { // 최대 재시도 횟수 6회
                        connectionRetryMap[gatt.device.address] = retries + 1
                        retryConnectionWithDelay(gatt.device, locationData)
                    } else {
                        Log.d(TAG, "최대 재연결 횟수 초과: ${gatt.device.address}")
                    }
                } else {
                    Log.e(TAG, "GATT 연결 실패 - 상태 코드: $status")
                    cleanupGattConnection(gatt)
                }
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                    handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    return
                }

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "서비스 발견 성공: ${gatt.device.address}")
                    val writeCharacteristic = getWriteCharacteristic(gatt)
                    writeCharacteristic?.let {
                        it.value = locationData
                        gatt.writeCharacteristic(it)
                        Log.d(TAG, "송신 특성에 위치 정보 작성 완료")
                    }
                } else {
                    Log.e(TAG, "서비스 발견 실패 - 상태 코드: $status")
                }
            }

            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onCharacteristicWrite(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                    handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
                    return
                }

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "데이터 작성 성공: ${gatt.device.address}")
                    gatt.disconnect()
                } else {
                    Log.e(TAG, "특성에 데이터 작성 실패 - 상태 코드: $status")
                }
            }
        }
    }

    private fun connectGattCompat(
        bluetoothGattCallback: BluetoothGattCallback,
        device: BluetoothDevice,
        autoConnect: Boolean
    ): BluetoothGatt? {
        return try {
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
                return null
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                device.connectGatt(context, autoConnect, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE)
            } else {
                device.connectGatt(context, autoConnect, bluetoothGattCallback)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "권한이 없어 GATT 연결에 실패했습니다.", e)
            null
        }
    }

    private fun cleanupGattConnection(gatt: BluetoothGatt) {
        try {
            if (!isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT)) {
                handleMissingPermission(Manifest.permission.BLUETOOTH_CONNECT)
                return
            }
            gatt.disconnect()
            gatt.close()
            connectedDevices.remove(gatt.device.address)
            Log.d(TAG, "GATT connection cleaned up for device: ${gatt.device.address}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException 발생: 권한 문제로 연결 해제 실패", e)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun retryConnectionWithDelay(device: BluetoothDevice, locationData: ByteArray) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!connectedDevices.contains(device.address)) {
                deviceQueue.addFirst(device)
                bluetoothGattCallback = createBluetoothGattCallback(locationData)
                bleGatt = connectGattCompat(bluetoothGattCallback!!, device, true)
            }
        }, 4000)
    }

    fun getWriteCharacteristic(gatt: BluetoothGatt): BluetoothGattCharacteristic? {
        val service = gatt.getService(UUID.fromString(SERVICE_UUID))
        return service?.getCharacteristic(UUID.fromString(CHARACTERISTIC_WRITE_UUID))
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun handleMissingPermission(permission: String) {
        Log.e(TAG, "$permission 권한이 없습니다.")
        // 권한이 없을 경우의 처리 추가 (예: 사용자에게 권한 요청 또는 안내 메시지)
    }
}
