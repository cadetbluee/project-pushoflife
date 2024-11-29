package com.example.PushOfLife.service;

import com.example.PushOfLife.dto.location.LocationDTO;
import com.example.PushOfLife.entity.MongoTokenEntity;
import com.example.PushOfLife.repository.MongoTokenRepository;
import com.mongodb.client.MongoClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisGeoCommands;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final KafkaProducerService kafkaProducerService;
    private final MongoTokenRepository mongoTokenRepository;

    public String generateShortUserId(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            // 해시를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // 원하는 길이로 잘라서 반환 (예: 8자리)
            return hexString.toString().substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveLocation(LocationDTO locationDTO) {

        String userId = generateShortUserId(locationDTO.getFcmToken());
        if (!mongoTokenRepository.findById(userId).isPresent()) {
            MongoTokenEntity mongoTokenEntity = new MongoTokenEntity();
            mongoTokenEntity.setId(userId);
            mongoTokenEntity.setFcmToken(locationDTO.getFcmToken());

            mongoTokenRepository.save(mongoTokenEntity);
        }
        System.out.println(userId);

        redisTemplate.opsForGeo().add("user_location", new Point( locationDTO.getLongitude(), locationDTO.getLatitude()), userId);

    }

    public List<String> findUsersNearby(String fcmToken, int range) {
        String userId = generateShortUserId(fcmToken);
        GeoReference reference = GeoReference.fromMember(userId);

        Distance radius = new Distance(range, RedisGeoCommands.DistanceUnit.METERS);
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(250);

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOperations
                .search("user_location", reference, radius, args);

        List<Point> position = geoOperations.position("user_location", userId);
        System.out.println(position.get(0));

        Point point = position.get(0);

        // fcm Token 리스트
        List<String> list = new ArrayList<>();

        if (results == null){
            return list;
        }

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> geoResult : results){
            String token = geoResult.getContent().getName();
            MongoTokenEntity mongoTokenEntity = mongoTokenRepository.findById(token).orElse(null);

            if (mongoTokenEntity != null && !mongoTokenEntity.getFcmToken().equals(fcmToken)){
                list.add(mongoTokenEntity.getFcmToken());
                kafkaProducerService.sendTopic(mongoTokenEntity.getFcmToken(), point.getX(),point.getY());
            }

        }

//        for (String tokens : list){
//            kafkaProducerService.sendTopic(tokens, point.getX(),point.getY());
//        }

        return list;

    }


}
