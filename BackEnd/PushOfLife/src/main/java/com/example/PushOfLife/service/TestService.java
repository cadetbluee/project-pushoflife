package com.example.PushOfLife.service;

import com.example.PushOfLife.dto.test.TestDTO;
import com.example.PushOfLife.entity.TestEntity;
import com.example.PushOfLife.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    @Autowired
    public final TestRepository testRepository;

    public TestEntity geTestEntity(Integer id) {
        return testRepository.findById(id).orElse(null);
    }

    public TestEntity createTestEntity(TestDTO testDTO) {
        TestEntity testEntity = TestEntity.builder()
                .test(testDTO.getTest())
                .build();

        return testRepository.save(testEntity);

    }
}
