package com.gome.pop.fup.easyid.util;

import redis.clients.jedis.ShardedJedis;

/**
 * jedis命令接口
 * Created by fupeng-ds on 2017/8/9.
 */
public interface JedisCommand<T> {

    T command(ShardedJedis jedis);
}
