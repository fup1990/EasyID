package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.server.Server;
import com.gome.pop.fup.easyid.snowflake.Snowflake;
import com.gome.pop.fup.easyid.util.*;
import com.gome.pop.fup.easyid.zk.ZkClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * 请求处理handler
 * Created by fupeng-ds on 2017/8/3.
 */
public class Handler extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = Logger.getLogger(Handler.class);

    private JedisUtil jedisUtil;

    private Server server;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        if (request.getType() == MessageType.REQUEST_TYPE_CREATE) {
            long begin = System.currentTimeMillis();
            try {
                server.pushIdsInRedis();
                LOGGER.info("handler run time:" + (System.currentTimeMillis() - begin));
            } finally {
                jedisUtil.del(Constant.REDIS_SETNX_KEY);
                //jedisUtil.returnResource(jedis);
                //zkClient.decrease(ip);
                //ctx.writeAndFlush("").addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    public Handler(Server server, JedisUtil jedisUtil) {
        this.server = server;
        this.jedisUtil = jedisUtil;
    }

    public JedisUtil getJedisUtil() {
        return jedisUtil;
    }

    public void setJedisUtil(JedisUtil jedisUtil) {
        this.jedisUtil = jedisUtil;
    }

}
