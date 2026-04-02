package com.example.admin.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 登录防暴力破解工具
 * 限制同一用户名在指定时间内的登录失败次数
 */
@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final RedisUtil redisUtil;

    /**
     * 登录失败次数 Redis key 前缀
     */
    private static final String LOGIN_FAIL_KEY_PREFIX = "login:fail:";

    /**
     * 锁定用户 Redis key 前缀
     */
    private static final String LOGIN_LOCK_KEY_PREFIX = "login:lock:";

    /**
     * 最大登录失败次数
     */
    private static final int MAX_FAIL_COUNT = 5;

    /**
     * 登录失败计数窗口（秒），5分钟内
     */
    private static final int FAIL_WINDOW_SECONDS = 300;

    /**
     * 账户锁定时间（秒），锁定30分钟
     */
    private static final int LOCK_SECONDS = 1800;

    /**
     * 检查用户是否被锁定
     *
     * @param username 用户名
     * @return true-被锁定，false-未锁定
     */
    public boolean isLocked(String username) {
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        return redisUtil.hasKey(lockKey);
    }

    /**
     * 记录登录失败
     * 如果达到最大失败次数，锁定账户
     *
     * @param username 用户名
     */
    public void recordFail(String username) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;

        // 增加失败次数
        long failCount = redisUtil.incr(failKey, 1);

        // 第一次失败时设置过期时间
        if (failCount == 1) {
            redisUtil.expire(failKey, FAIL_WINDOW_SECONDS);
        }

        // 达到最大失败次数，锁定账户
        if (failCount >= MAX_FAIL_COUNT) {
            String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
            redisUtil.set(lockKey, "1", LOCK_SECONDS);
            // 清除失败计数
            redisUtil.del(failKey);
        }
    }

    /**
     * 登录成功，清除失败记录
     *
     * @param username 用户名
     */
    public void recordSuccess(String username) {
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        redisUtil.del(failKey, lockKey);
    }

    /**
     * 获取剩余登录尝试次数
     *
     * @param username 用户名
     * @return 剩余尝试次数，如果已锁定返回0
     */
    public int getRemainingAttempts(String username) {
        if (isLocked(username)) {
            return 0;
        }
        String failKey = LOGIN_FAIL_KEY_PREFIX + username;
        Object count = redisUtil.get(failKey);
        if (count == null) {
            return MAX_FAIL_COUNT;
        }
        return Math.max(0, MAX_FAIL_COUNT - ((Number) count).intValue());
    }

    /**
     * 获取账户锁定剩余时间（秒）
     *
     * @param username 用户名
     * @return 锁定剩余秒数，未锁定返回0
     */
    public long getLockRemainingSeconds(String username) {
        String lockKey = LOGIN_LOCK_KEY_PREFIX + username;
        if (!redisUtil.hasKey(lockKey)) {
            return 0;
        }
        return redisUtil.getExpire(lockKey);
    }
}
