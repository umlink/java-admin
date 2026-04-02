package com.example.admin.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类，仅封装项目实际用到的操作
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取 key 的剩余过期时间（秒）
     * 返回 -1 表示永久有效，-2 表示 key 不存在
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 设置 key 的过期时间（秒），time <= 0 时不设置
     */
    public void expire(String key, long seconds) {
        if (seconds > 0) {
            redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        }
    }

    /**
     * 删除一个或多个 key
     */
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            redisTemplate.delete(List.of(keys));
        }
    }

    /**
     * 获取 String 类型缓存值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置 String 类型缓存（不带过期时间）
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置 String 类型缓存并指定过期时间（秒）
     * seconds <= 0 时退化为不带 TTL 的写入
     */
    public void set(String key, Object value, long seconds) {
        if (seconds > 0) {
            redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
        } else {
            set(key, value);
        }
    }

    /**
     * 原子递增
     *
     * @param delta 步长，必须大于 0
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta <= 0) {
            throw new IllegalArgumentException("递增步长必须大于 0");
        }
        Long result = redisTemplate.opsForValue().increment(key, delta);
        if (result == null) {
            throw new IllegalStateException("Redis incr 返回值为 null，key: " + key);
        }
        return result;
    }
}