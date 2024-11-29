package com.example.PushOfLife.repository;

import com.example.PushOfLife.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity,Integer> {

}
