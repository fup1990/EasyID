package com.gome.pop.fup.easyid.server;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * EasyID服务启动入口
 * Created by fupeng-ds on 2017/8/2.
 */
public class BootstrapWithRedis {

    /**
     * 例：java -jar EasyID.jar -zookeeper127.0.0.1:2181 -redis127.0.0.6379
     * @param args
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {

        /*String zookeeperAddres = "";
        String redisAddress = "";
        for (String arg : args) {
            if (arg.contains("-zookeeper")) zookeeperAddres = arg.split("-zookeeper")[1];
            if (arg.contains("-redis")) redisAddress = arg.split("-redis")[1];
        }
        if ("".equals(zookeeperAddres)) {
            throw new ZooKeeperNoAddressException("没有zookeeper地址");
        }
        if ("".equals(redisAddress)) {
            throw new RedisNoAddressException("没有redis地址");
        }
        Server server = new Server(zookeeperAddres, redisAddress);
        server.start();
        //自动管理workerid与datacenterid
        ZkClient zkClient = server.getZkClient();
        Snowflake snowflake = server.getSnowflake();
        int size = zkClient.getRootChildrenSize();
        if (size > 31) {
            int times = size/31;
            size = size - (31 * times);
        }
        snowflake.setWorkerId(size);
        snowflake.setWorkerId(size);*/
    }
}
