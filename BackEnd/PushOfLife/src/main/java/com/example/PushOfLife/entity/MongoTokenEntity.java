package com.example.PushOfLife.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Sharded;

@Getter
@Setter
@Document(collection = "locationCollection")
@Sharded(shardKey = { "fcmToken", "id" })
public class MongoTokenEntity {
    @Id
    private String id; // Redis에 사용할 사용자 ID
    private String fcmToken;

}
