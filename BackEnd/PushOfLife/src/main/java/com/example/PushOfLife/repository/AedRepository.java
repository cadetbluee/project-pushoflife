package com.example.PushOfLife.repository;

import com.example.PushOfLife.entity.AedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AedRepository extends JpaRepository<AedEntity, Integer> {

    List<AedEntity> findByAedLatitudeBetweenAndAedLongitudeBetween(double souLatitude, double norLatitude,
                                                             double souLongitude, double norLongitude);
}
