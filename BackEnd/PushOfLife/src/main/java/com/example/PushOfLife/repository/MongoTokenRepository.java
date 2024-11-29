package com.example.PushOfLife.repository;
import com.example.PushOfLife.entity.MongoTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoTokenRepository extends MongoRepository<MongoTokenEntity, String> {
}
