package com.gome.pop.fup.easyid.zk;

import com.gome.pop.fup.easyid.exception.ZooKeeperNoAddressException;
import com.gome.pop.fup.easyid.util.ConversionUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * ZooKeeper客户端类，在ZooKeeper上注册服务信息，并实现负载均衡
 * Created by fupeng-ds on 2017/8/3.
 */
public class ZkClient extends AbstractZkClient{

    private static final Logger logger = Logger.getLogger(ZkClient.class);

    private String address;

    public ZkClient() {
    }

    public ZkClient(String address) throws InterruptedException, IOException, KeeperException {
        this.address = address;
        connect();
    }

    /**
     * 初始化ZooKeeper客户端，并判断是否有根节点，没有则创建持久化节点
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void connect() throws IOException, KeeperException, InterruptedException {
        if (address == null || "".equals(address)) {
            throw new ZooKeeperNoAddressException("zookeeper address not null!");
        }
        zooKeeper = new ZooKeeper(address, SESSIONTIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
                logger.info("zookeeper type : " + event.getType() + " and state" + event.getState());
            }
        });
        Stat stat = zooKeeper.exists(ZK_ROOT_NODE, false);
        if (null == stat) {
            zooKeeper.create(ZK_ROOT_NODE, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        logger.info("ZooKeeper connected!");
    }

    /**
     * 初始化节点，并记录连接数为0
     * @param node
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void register(String node) throws KeeperException, InterruptedException, IOException {
        if (zooKeeper == null) connect();
        if (hasRegisted(node)) return;
        zooKeeper.create(getNodePath(node), ConversionUtil.intToByteArray(0), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        logger.info("ZooKeeper registered!");
    }

    /**
     * 校验当前节点是否已经存在
     * @param node
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private boolean hasRegisted(String node) throws InterruptedException, IOException {
        try {
            return zooKeeper.exists(getNodePath(node), false) == null ? false : true;
        } catch (KeeperException e) {
            logger.error("zookeeper session time out!");
            logger.error(e.getMessage());
            zooKeeper = new ZooKeeper(address, SESSIONTIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    logger.info("zookeeper type : " + event.getType() + ", and state" + event.getState());
                }
            });
            return hasRegisted(node);
        }
    }

    /**
     * 增加节点的记录数
     * @param node
     * @throws KeeperException
     * @throws InterruptedException
     */
    public synchronized int increase(String node) throws KeeperException, InterruptedException, IOException {
        if (zooKeeper == null) connect();
        int count = getCount(node);
        zooKeeper.setData(getNodePath(node), ConversionUtil.intToByteArray(++count), -1);
        logger.info("ZooKeeper increase count:" + count);
        return count;
    }

    /**
     * 减少节点的记录数
     * @param node
     * @throws KeeperException
     * @throws InterruptedException
     */
    public synchronized int decrease(String node) throws KeeperException, InterruptedException, IOException {
        if (zooKeeper == null) connect();
        int count = getCount(node);
        if (count > 0) {
            zooKeeper.setData(getNodePath(node), ConversionUtil.intToByteArray(--count), -1);
        }
        logger.info("ZooKeeper decrease count:" + count);
        return count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
