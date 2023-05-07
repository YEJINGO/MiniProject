package com.example.miniproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;

    public void setValues(String token, String userId) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(token, userId, Duration.ofMinutes(3));
    }

    public String getValues(String token) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    public void deleteValues(String token) {
        redisTemplate.delete(token);
    }
}
