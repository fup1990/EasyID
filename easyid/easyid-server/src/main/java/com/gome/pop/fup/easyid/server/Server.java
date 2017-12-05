package com.gome.pop.fup.easyid.server;

import com.gome.pop.fup.easyid.handler.DecoderHandler;
import com.gome.pop.fup.easyid.handler.Handler;
import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.snowflake.Snowflake;
import com.gome.pop.fup.easyid.util.*;
import com.gome.pop.fup.easyid.zk.ZkClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import redis.clients.jedis.ShardedJedis;

import java.io.IOException;

/**
 * 服务端，接收创建id的请求
 * Created by fupeng-ds on 2017/8/2.
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    private Snowflake snowflake;

    private ZkClient zkClient;

    private String redisAddress;

    private String zookeeperAddres;

    private JedisUtil jedisUtil;

    public Server(String zookeeperAddres, String redisAddress) {
        this.zookeeperAddres = zookeeperAddres;
        this.redisAddress = redisAddress;
    }

    public void start() throws Exception {
        snowflake = new Snowflake();
        jedisUtil = JedisUtil.newInstance(redisAddress);
        String localHost = IpUtil.getLocalHost();
        Cache.set(Constant.LOCALHOST, localHost, -1l);
        zkClient = new ZkClient(zookeeperAddres);
        zkClient.register(localHost);
        //查看redis中是否有id,没有则创建
        pushIdsInRedis();
        logger.info("EasyID Server started!");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                zkClient.close();
                jedisUtil.close();
            }
        }));
        //启动服务
        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
    }

    public void pushIdsInRedis() throws KeeperException, InterruptedException {
        ShardedJedis jedis = jedisUtil.getJedis();
        String ip = (String) Cache.get(Constant.LOCALHOST);
        try {
            int redis_list_size = zkClient.getRedisListSize();
            Long len = jedis.llen(Constant.REDIS_LIST_NAME);
            if (null == len) len = 0l;
            if (len < (redis_list_size * 300)) {
                //批量生成id
                long[] ids = snowflake.nextIds((redis_list_size * 1000) - len.intValue());
                String[] strs = ConversionUtil.longsToStrings(ids);
                //将生成的id存入redis队列
                jedis.rpush(Constant.REDIS_LIST_NAME, strs);
                jedis.expire(Constant.REDIS_LIST_NAME, 1800);
            }
        } finally {
            jedisUtil.returnResource(jedis);
            //zkClient.increase(ip);
            new Thread(new IncreaseRunnable(zkClient, ip)).start();
        }
    }

    /**
     * 接收线程，用于接收客户端请求
     */
    private class AcceptThread extends Thread {

        private EventLoopGroup bossGroup;

        private EventLoopGroup workerGroup;

        private ServerBootstrap bootstrap;

        public AcceptThread() {
            bossGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(8);
            bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel)
                                throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new DecoderHandler(Request.class))
                                    .addLast(new Handler(Server.this, jedisUtil));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        @Override
        public void run() {
            try {
                ChannelFuture future = bootstrap.bind(IpUtil.getLocalHost(), Constant.EASYID_SERVER_PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        }
    }

    private class IncreaseRunnable implements Runnable {

        private ZkClient zkClient;

        private String ip;

        public IncreaseRunnable(ZkClient zkClient, String ip) {
            this.zkClient = zkClient;
            this.ip = ip;
        }

        public void run() {
            try {
                zkClient.increase(ip);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Snowflake getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    public String getZookeeperAddres() {
        return zookeeperAddres;
    }

    public void setZookeeperAddres(String zookeeperAddres) {
        this.zookeeperAddres = zookeeperAddres;
    }

    public String getRedisAddress() {
        return redisAddress;
    }

    public void setRedisAddress(String redisAddress) {
        this.redisAddress = redisAddress;
    }
}
