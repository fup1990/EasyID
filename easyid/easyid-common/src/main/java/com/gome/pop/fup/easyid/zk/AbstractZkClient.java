package com.gome.pop.fup.easyid.zk;

import com.gome.pop.fup.easyid.util.Cache;
import com.gome.pop.fup.easyid.util.Constant;
import com.gome.pop.fup.easyid.util.ConversionUtil;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *ZkClient抽象类
 * Created by fupeng-ds on 2017/8/3.
 */
public abstract class AbstractZkClient {

    private static final Logger logger = Logger.getLogger(AbstractZkClient.class);

    /**
     * ZooKeeper上的根节点
     */
    protected final String ZK_ROOT_NODE = "/EasyID";

    /**
     * ZooKeeper客户端
     */
    protected ZooKeeper zooKeeper;

    /**
     *ZooKeeper服务地址
     */
    protected String address;

    private Lock lock = new ReentrantLock();

    /**
     * ZooKeeper会话有效时间
     */
    protected final static int SESSIONTIMEOUT = 10000;

    /**
     * 关闭zookeeper连接
     * @throws InterruptedException
     */
    public void close() {
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }

    public int getCount(String node) throws KeeperException, InterruptedException {
        byte[] data = zooKeeper.getData(getNodePath(node), null, null);
        return ConversionUtil.byteArrayToInt(data);
    }

    protected String getNodePath(String node) {
        return ZK_ROOT_NODE + "/" + node;
    }

    public void createZooKeeper() throws IOException {
        zooKeeper = new ZooKeeper(address, SESSIONTIMEOUT, new Watcher() {
            public void process(WatchedEvent event) {
                logger.info("zookeeper type : " + event.getType() + ", and state" + event.getState());
            }
        });
    }

    /**
     * 获取/EasyID根节点下子节点的数量
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public int getRootChildrenSize() throws KeeperException, InterruptedException {
        List<String> nodes = zooKeeper.getChildren(ZK_ROOT_NODE, null, null);
        return nodes.size();
    }

    /**
     * 获取/EasyID根节点下子节点的数量
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public List<String> getRootChildren() throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(ZK_ROOT_NODE, null, null);
    }

    /**
     * redis队列数量
     * 动态获取
     * @return
     */
    public int getRedisListSize() {
        int size;
        if (Cache.hasKey(Constant.REDIS_LIST_SIZE)) {
            size =  (Integer)Cache.get(Constant.REDIS_LIST_SIZE);
        } else {
            try {
                size = this.getRootChildrenSize();
                if (lock.tryLock()) {
                    try {
                        //设置有效时间60s
                        Cache.set(Constant.REDIS_LIST_SIZE, size, 60l);
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (Exception e) {
                logger.error(e);
                size = 1;
            }
        }
        return size;
    }


}
