package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.model.Response;
import com.gome.pop.fup.easyid.snowflake.Snowflake;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by fupeng on 2017/12/9.
 */
public class NettyHandler extends SimpleChannelInboundHandler<Request> {

    private Snowflake snowflake;

    public NettyHandler(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        Response response = new Response();
        long id = snowflake.nextId();
        response.setId(id);
        response.setFinish(true);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
