package com.example.PushOfLife.controller;

import com.example.PushOfLife.dto.aed.*;
import com.example.PushOfLife.entity.AedAvailableEntity;
import com.example.PushOfLife.service.AedLocationService;
import com.example.PushOfLife.service.AedService;
import com.example.PushOfLife.service.CsvToDataBaseService;
import com.example.PushOfLife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/POL/aed")
@RequiredArgsConstructor
public class AedController {

    private final AedService aedService;
    private final CsvToDataBaseService csvToDataBaseService;
    private final UserService userService;
    private final AedLocationService aedLocationService;

    @GetMapping("/list/api")
    public ResponseEntity<?> getAedLoList() {
        try {
            aedService.fetchAllAedData();
            return ResponseEntity.status(HttpStatus.OK).body("completed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/list")
    public ResponseEntity<?> saveAedLoList() {
        try {
            csvToDataBaseService.processCsv("aed_data.csv");
            return ResponseEntity.status(HttpStatus.OK).body("saved completed");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllAedLoList() {
        List<AedResponseDTO> responseList = aedLocationService.getAllAedList();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping("/within-bounds")
    public ResponseEntity<?> getAedWithinBounds(@RequestParam("nor_latitude") double norLatitude, @RequestParam("nor_longitude") double norLongitude,
                                                @RequestParam("sou_latitude") double souLatitude,@RequestParam("sou_longitude")  double souLongitude) {
        List<AedResponseDTO> responseDTO= aedLocationService.getAedLocation(norLatitude, norLongitude, souLatitude, souLongitude);
        if (responseDTO == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AED not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/details/{aedId}")
    public ResponseEntity<?> getAedDetails(@PathVariable Integer aedId) {
        AedDetailsResponseDTO responseDTO = aedLocationService.getAedDetails(aedId);
        if (responseDTO == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AED not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/add/info")
    public ResponseEntity<?> addAed(@RequestBody AedAddRequestDTO requestDTO) {
        aedLocationService.addAed(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @PostMapping("/add/details")
    public ResponseEntity<?> addAedDetails(@RequestBody AedDetailsRequestDTO requestDTO) {
        aedLocationService.addAedDetails(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("success");
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAedAvailableDetails() {
        List<AedAvailableResponseDTO> responseDTO = aedLocationService.getAedAvailableDetails();
        if (responseDTO == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AED Available not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("all")
    public ResponseEntity<?> getAllAedDetails() {
        List<AllAedResponseDTO> responseList = aedLocationService.getAllAed();
        if (responseList == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AED not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
}
