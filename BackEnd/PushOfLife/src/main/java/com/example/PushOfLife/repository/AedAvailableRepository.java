package com.example.PushOfLife.repository;

import com.example.PushOfLife.entity.AedAvailableEntity;
import com.example.PushOfLife.entity.AedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AedAvailableRepository extends JpaRepository<AedAvailableEntity, Integer> {

    Optional<AedAvailableEntity> findByAedEntity(AedEntity aed);
}
