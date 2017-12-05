package com.gome.pop.fup.easyid.zk;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * Created by fupeng-ds on 2017/8/3.
 */
public class ZkClient extends AbstractZkClient{

    /**
     * 构造方法
     * @param address
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public ZkClient(String address) throws IOException, KeeperException, InterruptedException {
        this.address = address;
        zooKeeper = new ZooKeeper(address, SESSIONTIMEOUT, null);
    }

    /**
     * 负载均衡算法：zk中连接数最少的节点ip
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String balance() throws KeeperException, InterruptedException {
        List<String> nodes = zooKeeper.getChildren(ZK_ROOT_NODE, null);
        //若只有一台注册了服务，则直接返回这唯一的一台服务
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        String result = null;
        int min = 0;
        for (int i = 0; i < nodes.size(); i++) {
            int count = getCount(nodes.get(i));
            if (i == 0 || count < min) {
                min = count;
                result = nodes.get(i);
            }
        }
        return result;
    }
}
