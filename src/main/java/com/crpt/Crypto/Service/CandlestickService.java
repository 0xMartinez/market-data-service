package com.crpt.Crypto.Service;

import com.crpt.Crypto.Repository.CandlestickRepository;
import com.crpt.Crypto.Repository.Model.CandlestickDb;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandlestickService {

    private static final String CACHE_KEY = "CANDLESTICK_LIST";


    private final CandlestickRepository candlestickRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<CandlestickDb> getAllCandlesticks() throws JsonProcessingException {
        List<Object> cachedObjects = redisTemplate.opsForList().range(CACHE_KEY, 0, -1);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (cachedObjects != null && !cachedObjects.isEmpty()) {
            List<CandlestickDb> candlestickList = objectMapper.readValue(objectMapper.writeValueAsString(cachedObjects), new TypeReference<List<CandlestickDb>>() {});
            return candlestickList;
        }


        List<CandlestickDb> candlesticks = candlestickRepository.findAll();
        if (!candlesticks.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(CACHE_KEY, candlesticks);
            redisTemplate.expire(CACHE_KEY, 10, TimeUnit.MINUTES);
        }

        return candlesticks;
    }

    public void saveCandlestick(CandlestickDb candlestick) {

        redisTemplate.opsForList().rightPushAll(CACHE_KEY, candlestick);
        redisTemplate.expire(CACHE_KEY, 1, TimeUnit.DAYS);
    }

    public void deleteCacheCandlesticks() {

        redisTemplate.delete(CACHE_KEY);
        List<Object> cachedObjects = redisTemplate.opsForList().range(CACHE_KEY, 0, -1);

        log.info("{}", cachedObjects);
    }

    public void getCacheCandlesticks() {

        List<Object> cachedObjects = redisTemplate.opsForList().range(CACHE_KEY, 0, -1);

        log.info("{}", cachedObjects);
    }
}