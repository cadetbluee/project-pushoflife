package com.example.PushOfLife.service;

import com.example.PushOfLife.dto.aed.*;
import com.example.PushOfLife.entity.AedAvailableEntity;
import com.example.PushOfLife.entity.AedEntity;
import com.example.PushOfLife.repository.AedAvailableRepository;
import com.example.PushOfLife.repository.AedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AedLocationService {

    private final AedRepository aedRepository;
    private final AedAvailableRepository aedAvailableRepository;

    public List<AedResponseDTO> getAedLocation(Double norLatitude, Double norLongitude, Double southLatitude, Double southLongitude) {
        List<AedEntity> findAed = aedRepository.findByAedLatitudeBetweenAndAedLongitudeBetween(southLatitude, norLatitude, southLongitude, norLongitude);
        List<AedResponseDTO> aedList = new ArrayList<>();
        for (AedEntity aedEntity : findAed) {
            AedResponseDTO aedResponseDTO = new AedResponseDTO();
            aedResponseDTO.fromEntity(aedEntity);
            aedList.add(aedResponseDTO);
        }
        return aedList;
    }

    public AedDetailsResponseDTO getAedDetails(Integer aedId){
        AedEntity findAed = aedRepository.findById(aedId).orElseThrow(() -> new RuntimeException("Aed Not Found"));
        AedAvailableEntity findAedAvailable = aedAvailableRepository.findById(aedId).orElseThrow(() -> new RuntimeException("AedAvailable Not Found"));
        AedDetailsResponseDTO aedResponseDTO = new AedDetailsResponseDTO();
        aedResponseDTO.fromEntity(findAed, findAedAvailable);
        return aedResponseDTO;
    }

    public void addAed(AedAddRequestDTO requestDTO) {
        System.out.println(requestDTO.getAedLatitude());
        System.out.println(requestDTO.getAedLongitude());
        AedEntity aedEntity = AedEntity.builder()
                .aedLatitude(requestDTO.getAedLatitude())
                .aedLongitude(requestDTO.getAedLongitude()) // 여기 오타 수정
                .aedLocation(requestDTO.getAedLocation())
                .aedAddress(requestDTO.getAedAddress())
                .aedPlace(requestDTO.getAedPlace())
                .aedNumber(requestDTO.getAedNumber())
                .build();
        aedRepository.save(aedEntity);
    }

    public void addAedDetails(AedDetailsRequestDTO requestDTO){
        AedEntity findAed = aedRepository.findById(requestDTO.getAedId()).orElseThrow(() -> new RuntimeException("Aed Not Found"));
        AedAvailableEntity aedAvailableEntity = AedAvailableEntity.builder()
                .aedEntity(findAed)
                .aedMonStTime(requestDTO.getAedMonStTime())
                .aedMonEndTime(requestDTO.getAedMonEndTime())
                .aedTueStTime(requestDTO.getAedTueStTime())
                .aedTueEndTime(requestDTO.getAedTueEndTime())
                .aedWedStTime(requestDTO.getAedWedStTime())
                .aedWedEndTime(requestDTO.getAedWedEndTime())
                .aedThuStTime(requestDTO.getAedThuStTime())
                .aedThuEndTime(requestDTO.getAedThuEndTime())
                .aedFriStTime(requestDTO.getAedFriStTime())
                .aedFriEndTime(requestDTO.getAedFriEndTime())
                .aedSatStTime(requestDTO.getAedSatStTime())
                .aedSatEndTime(requestDTO.getAedSatEndTime())
                .aedSunStTime(requestDTO.getAedSunStTime())
                .aedSunEndTime(requestDTO.getAedSunEndTime())
                .aedHolStTime(requestDTO.getAedHolStTime())
                .aedHolEndTime(requestDTO.getAedHolEndTime())
                .aedFirSun(requestDTO.getAedFirSun())
                .aedSecSun(requestDTO.getAedSecSun())
                .aedThiSun(requestDTO.getAedThiSun())
                .aedFouSun(requestDTO.getAedFouSun())
                .build();
        aedAvailableRepository.save(aedAvailableEntity);
    }

    public List<AedResponseDTO> getAllAedList(){
        List<AedEntity> findAedList = aedRepository.findAll();
        List<AedResponseDTO> aedList = new ArrayList<>();
        for (AedEntity aedEntity : findAedList) {
            AedResponseDTO aedResponseDTO = new AedResponseDTO();
            aedResponseDTO.fromEntity(aedEntity);
            aedList.add(aedResponseDTO);
        }
        return aedList;
    }

    public List<AedAvailableResponseDTO> getAedAvailableDetails(){
        List<AedAvailableEntity> aedEntityList = aedAvailableRepository.findAll();
        List<AedAvailableResponseDTO> aedDetailsList = new ArrayList<>();
        for (AedAvailableEntity aedAvailableEntity : aedEntityList) {
            AedAvailableResponseDTO aedAvailableResponseDTO = new AedAvailableResponseDTO();
            aedAvailableResponseDTO.fromEntity(aedAvailableEntity);
            aedDetailsList.add(aedAvailableResponseDTO);
        }
        return aedDetailsList;
    }

    public List<AllAedResponseDTO> getAllAed(){
        List<AedEntity> aedEntityList = aedRepository.findAll();
        List<AllAedResponseDTO> allAedList = new ArrayList<>();
        for(AedEntity aed : aedEntityList){
            AedAvailableEntity findAvailable = aedAvailableRepository.findByAedEntity(aed).orElseThrow(()->new RuntimeException("Aed Available Not Found"));
            AllAedResponseDTO allAedResponseDTO = new AllAedResponseDTO();
            allAedResponseDTO.fromEntity(aed, findAvailable);
            allAedList.add(allAedResponseDTO);
            System.out.println(aed.getId());
        }
        return allAedList;
    }
}
