package com.example.loginservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SessionService {
    private static final String SESSION_KEY_PREFIX = "spring:session:";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    // 세션 저장
    public void saveSession(String sessionId, Map<String, Object> sessionData) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().putAll(redisKey, sessionData);
    }
    // 세션 조회
    public Map<Object, Object> getSession(String sessionId) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        return redisTemplate.opsForHash().entries(redisKey);
    }
    // 세션 속성 업데이트
    public void updateSessionAttribute(String sessionId, String key, Object value) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForHash().put(redisKey, key, value);
    }
    // 세션 속성 조회
    public Object getSessionAttribute(String sessionId, String key) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        return redisTemplate.opsForHash().get(redisKey, key);
    }
    // 세션 삭제
    public void deleteSession(String sessionId) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.delete(redisKey);
    }
}
