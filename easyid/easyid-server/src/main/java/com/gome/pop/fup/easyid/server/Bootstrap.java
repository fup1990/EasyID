package com.gome.pop.fup.easyid.server;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

/**
 * EasyID服务启动入口
 * Created by fupeng-ds on 2017/8/2.
 */
public class Bootstrap {

    /**
     * 例：java -jar EasyID.jar -zookeeper127.0.0.1:2181 -redis127.0.0.6379
     * @param args
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.startup();
    }
}
