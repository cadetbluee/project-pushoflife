package com.example.PushOfLife.repository;
import com.example.PushOfLife.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("SELECT u.userFcm FROM UserEntity u WHERE u.userFcm IS NOT NULL ")
    List<String> findAllWithFCM();
}
