package com.gome.pop.fup.easyid.util;

import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fupeng-ds on 2017/8/9.
 */
public class JedisUtil {

    private static ShardedJedisPool jedisPool;

    private static final JedisUtil util = new JedisUtil();

    /**
     * 有效时间半个小时
     */
    private int EXPIRE = 30 * 60;

    private JedisUtil() {
    }

    public synchronized static JedisUtil newInstance(String redisAddress) {
        if (jedisPool == null) {
            String[] ips = redisAddress.split(",");
            init(ips);
        }
        return util;
    }

    private static void init(String[] ips) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(300);
        config.setMaxTotal(-1);
        config.setMaxWaitMillis(30000);
        config.setTestOnBorrow(true);
        List<JedisShardInfo> infos = new ArrayList<JedisShardInfo>(ips.length);
        for (String ip : ips) {
            String[] split = ip.split(":");
            JedisShardInfo info = new JedisShardInfo(split[0], Integer.valueOf(split[1]));
            infos.add(info);
        }
        jedisPool = new ShardedJedisPool(config, infos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    public synchronized ShardedJedis getJedis() {
        return jedisPool.getResource();
    }

    public void returnResource(ShardedJedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public String set(final String key, final String value) {
        return (String)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                String set = jedis.set(key, value);
                jedis.expire(key, EXPIRE);
                return set;
            }
        });
    }

    public ShardedJedisPipeline getPipeline() {
        return (ShardedJedisPipeline)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.pipelined();
            }
        });
    }

    public String get(final String key) {
        return (String)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public Boolean exists(final String key) {
        return (Boolean)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.exists(key);
            }
        });
    }

    public Long llen(final String key) {
        return (Long)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.llen(key);
            }
        });
    }

    public String lpop(final String key) {
        return (String)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.lpop(key);
            }
        });
    }

    public Long rpush(final String key, final String value) {
        return (Long)command(new JedisCommand<Object>() {
            public Long command(ShardedJedis jedis) {
                return jedis.rpush(key, value);
            }
        });
    }

    public Long rpush(final String key, final String[] value) {
        return (Long)command(new JedisCommand<Object>() {
            public Long command(ShardedJedis jedis) {
                return jedis.rpush(key, value);
            }
        });
    }

    public Long setnx(final String key, final String value, final int seconds) {
        return (Long)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                Long result = jedis.setnx(key, value);
                jedis.expire(key, seconds);
                return result;
            }
        });
    }

    public Long expire(final String key, final int seconds) {
        return (Long)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.expire(key, seconds);
            }
        });
    }

    public Long del(final String key) {
        return (Long)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.del(key);
            }
        });
    }

    public Long incr(final String key) {
        return (Long)command(new JedisCommand<Object>() {
            public Object command(ShardedJedis jedis) {
                return jedis.incr(key);
            }
        });
    }

    public void close() {
        jedisPool.close();
    }

    private Object command(JedisCommand<Object> command) {
        ShardedJedis jedis = getJedis();
        try {
            return command.command(jedis);
        } finally {
            returnResource(jedis);
        }
    }
}
