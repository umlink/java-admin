package com.example.admin.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 */
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // ============================== common ==============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间（秒）
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键（不能为 null）
     * @return 时间（秒），返回 -1 代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在，false 不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) List.of(key));
            }
        }
    }

    // ============================== String ==============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true 成功，false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间（秒），如果 time 小于等于 0 将设置无限期
     * @return true 成功，false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几（大于0）
     * @return 递增后的值
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几（小于0）
     * @return 递减后的值
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ============================== Hash ==============================

    /**
     * HashGet
     *
     * @param key  键（不能为 null）
     * @param item 项（不能为 null）
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取 hashKey 对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功，false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间（秒）
     * @return true 成功，false 失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张 hash 表中放入数据，如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功，false 失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张 hash 表中放入数据，如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间（秒），注意：如果已存在的 hash 表有时间，这里将会替换原有的时间
     * @return true 成功，false 失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除 hash 表中的值
     *
     * @param key  键（不能为 null）
     * @param item 项（可以多个，不能为 null）
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断 hash 表中是否有该项的值
     *
     * @param key  键（不能为 null）
     * @param item 项（不能为 null）
     * @return true 存在，false 不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    // ============================== Set ==============================

    /**
     * 根据 key 获取 Set 中的所有值
     *
     * @param key 键
     * @return Set 集合
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据 value 从 Set 中查询是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在，false 不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入 Set 缓存
     *
     * @param key    键
     * @param values 值（可以多个）
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将数据放入 Set 缓存并设置时间
     *
     * @param key    键
     * @param time   时间（秒）
     * @param values 值（可以多个）
     * @return 成功个数
     */
    public long sSet(String key, long time, Object... values) {
        try {
            long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取 Set 缓存的长度
     *
     * @param key 键
     * @return Set 长度
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除 Set 缓存中值为 value 的项
     *
     * @param key    键
     * @param values 值（可以多个）
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ============================== List ==============================

    /**
     * 获取 List 缓存的内容
     *
     * @param key   键
     * @param start 开始索引（0 开始）
     * @param end   结束索引（-1 代表所有值）
     * @return List 内容
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 List 缓存的长度
     *
     * @param key 键
     * @return List 长度
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 根据索引获取 List 缓存中的值
     *
     * @param key   键
     * @param index 索引（index >= 0 时，0 表头，1 第二个元素；index < 0 时，-1 表尾，-2 倒数第二个元素）
     * @return List 对应索引的值
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将值放入 List 缓存（从右边添加）
     *
     * @param key   键
     * @param value 值
     * @return true 成功，false 失败
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将值放入 List 缓存并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间（秒）
     * @return true 成功，false 失败
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将多个值放入 List 缓存
     *
     * @param key    键
     * @param values 值
     * @return true 成功，false 失败
     */
    public boolean lSet(String key, List<Object> values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将多个值放入 List 缓存并设置时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间（秒）
     * @return true 成功，false 失败
     */
    public boolean lSet(String key, List<Object> values, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改 List 缓存中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return true 成功，false 失败
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除 List 缓存中值为 value 的项
     *
     * @param key   键
     * @param count 移除数量（count>0：从左开始移除 count 个；count<0：从右开始移除 -count 个；count=0：移除所有值为 value 的项）
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}