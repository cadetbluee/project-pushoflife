package com.example.PushOfLife.controller;

import com.example.PushOfLife.dto.location.LocationDTO;
import com.example.PushOfLife.dto.location.RequestHelpDTO;
import com.example.PushOfLife.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/POL/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping()
    public ResponseEntity<?> saveLocation(@RequestBody LocationDTO locationDTO) {
        try{
            locationService.saveLocation(locationDTO);
            return ResponseEntity.ok().body(locationDTO);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/help")
    public ResponseEntity<?> getNearLocation(@RequestBody RequestHelpDTO requestHelpDTO) {
        try{
            List<String> result = locationService.findUsersNearby(requestHelpDTO.getFcmToken(), 300);
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
