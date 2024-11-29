package com.example.pushoflife.ui.aedLocation

import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.pushoflife.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AEDDetailBottomSheet(
    private val address: String,
    private val place: String,
    private val location: String,
    private val number: String,
    private val monStart: String?,
    private val monEnd: String?,
    private val tueStart: String?,
    private val tueEnd: String?,
    private val wedStart: String?,
    private val wedEnd: String?,
    private val thuStart: String?,
    private val thuEnd: String?,
    private val friStart: String?,
    private val friEnd: String?,
    private val satStart: String?,
    private val satEnd: String?,
    private val sunStart: String?,
    private val sunEnd: String?,
    private val holStart: String?,
    private val holEnd: String?
) : BottomSheetDialogFragment() {

    // CustomTypefaceSpan 클래스 정의
    inner class CustomTypefaceSpan(private val customTypeface: Typeface) : TypefaceSpan("") {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, customTypeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, customTypeface)
        }

        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            paint.typeface = tf
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.RoundedTopBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aed_detail_bottom_sheet, container, false)
        view.setBackgroundResource(R.drawable.rounded_bottom_sheet_background)

        // 필요한 시간 형식을 변환하는 함수 정의
        fun formatTime(time: String?): String {
            return time?.substring(0, 5) ?: "" // 'HH:mm' 형식으로 잘라서 반환
        }

        view.findViewById<TextView>(R.id.addressTextView).text = address

        val placeTextView = view.findViewById<TextView>(R.id.placeTextView)
        placeTextView.text = place
        placeTextView.typeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)

        // CustomTypefaceSpan을 사용하여 설치 위치 텍스트에 '설치 위치'만 bold 폰트 적용
        val boldTypeface = ResourcesCompat.getFont(requireContext(), R.font.pretendard_bold)
        val locationTextView = view.findViewById<TextView>(R.id.locationTextView)
        val locationSpannable = SpannableString("설치위치 $location")
        boldTypeface?.let {
            locationSpannable.setSpan(
                CustomTypefaceSpan(it),
                0,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        locationTextView.text = locationSpannable

        // CustomTypefaceSpan을 사용하여 전화번호 텍스트에 '전화번호'만 bold 폰트 적용
        val numberTextView = view.findViewById<TextView>(R.id.numberTextView)
        val numberSpannable = SpannableString("전화번호 $number")
        boldTypeface?.let {
            numberSpannable.setSpan(
                CustomTypefaceSpan(it),
                0,
                5,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        numberTextView.text = numberSpannable

        // CustomTypefaceSpan을 사용하여 요일 텍스트에만 bold 폰트를 적용하는 함수
        fun setDayTextView(textView: TextView, day: String, start: String?, end: String?) {
            val timeText = if (start != null && end != null) "${formatTime(start)} - ${formatTime(end)}" else "운영 시간 없음"
            val spannable = SpannableString("$day $timeText")
            boldTypeface?.let {
                spannable.setSpan(
                    CustomTypefaceSpan(it),
                    0,
                    day.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.text = spannable
        }

        // 요일별 시간 설정
//        setDayTextView(view.findViewById(R.id.monTimeTextView), "월요일", monStart, monEnd)
//        setDayTextView(view.findViewById(R.id.tueTimeTextView), "화요일", tueStart, tueEnd)
//        setDayTextView(view.findViewById(R.id.wedTimeTextView), "수요일", wedStart, wedEnd)
//        setDayTextView(view.findViewById(R.id.thuTimeTextView), "목요일", thuStart, thuEnd)
//        setDayTextView(view.findViewById(R.id.friTimeTextView), "금요일", friStart, friEnd)
//        setDayTextView(view.findViewById(R.id.satTimeTextView), "토요일", satStart, satEnd)
//        setDayTextView(view.findViewById(R.id.sunTimeTextView), "일요일", sunStart, sunEnd)
//        setDayTextView(view.findViewById(R.id.holTimeTextView), "공휴일", holStart, holEnd)

        view.post {
            val bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            val parent = view.parent as View
            // behavior.peekHeight = 1000
            parent.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_bottom_sheet_background)
            // 초기 높이 설정 (예: 1000px)
            bottomSheetBehavior.peekHeight = 1000

            // 또는 전체 화면 높이로 설정
            // bottomSheetBehavior.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT

            // 상태를 확장으로 설정 (바텀시트가 처음에 확장된 상태로 나타나도록 함)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return view
    }
}
