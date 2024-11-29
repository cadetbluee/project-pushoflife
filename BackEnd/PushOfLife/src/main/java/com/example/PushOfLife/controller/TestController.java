package com.example.PushOfLife.controller;

import com.example.PushOfLife.dto.test.TestDTO;
import com.example.PushOfLife.entity.TestEntity;
import com.example.PushOfLife.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/POL/test")
@RequiredArgsConstructor
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping
    public ResponseEntity<?> getTest(@RequestParam int id) {
        TestEntity findTest = testService.geTestEntity(id);
        if (findTest == null) {
            return ResponseEntity.notFound().build();
        }else{
            return ResponseEntity.ok(findTest);
        }
    }

    @PostMapping
    public ResponseEntity<?> postTest(@RequestBody TestDTO test) {
        try{
            TestEntity createdTest = testService.createTestEntity(test);
            return ResponseEntity.ok(createdTest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        }


    }


}
