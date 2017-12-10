package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.model.Response;
import com.gome.pop.fup.easyid.server.Server;
import com.gome.pop.fup.easyid.snowflake.Snowflake;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * 请求处理handler
 * Created by fupeng-ds on 2017/8/3.
 */
public class Handler extends SimpleChannelInboundHandler<Request> {

    private static final Logger LOGGER = Logger.getLogger(Handler.class);

    private Server server;

    private Snowflake snowflake;

    public Handler(Server server) {
        this.server = server;
        this.snowflake = server.getSnowflake();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        Response response = new Response();
        response.setId(snowflake.nextId());
        response.setFinish(true);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
