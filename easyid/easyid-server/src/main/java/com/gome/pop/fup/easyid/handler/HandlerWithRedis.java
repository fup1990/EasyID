package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.server.ServerWithRedis;
import com.gome.pop.fup.easyid.util.Constant;
import com.gome.pop.fup.easyid.util.JedisUtil;
import com.gome.pop.fup.easyid.util.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * 请求处理handler
 * Created by fupeng-ds on 2017/8/3.
 */
public class HandlerWithRedis extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = Logger.getLogger(HandlerWithRedis.class);

    private JedisUtil jedisUtil;

    private ServerWithRedis server;

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

    public HandlerWithRedis(ServerWithRedis server, JedisUtil jedisUtil) {
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
