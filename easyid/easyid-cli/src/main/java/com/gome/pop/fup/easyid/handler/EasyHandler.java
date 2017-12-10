package com.gome.pop.fup.easyid.handler;

import com.gome.pop.fup.easyid.id.EasyID;
import com.gome.pop.fup.easyid.model.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by fupeng on 2017/12/10.
 */
public class EasyHandler extends SimpleChannelInboundHandler<Response>{

    private EasyID easyID;

    public EasyHandler(EasyID easyID) {
        this.easyID = easyID;
    }

    protected void channelRead0(ChannelHandlerContext ctx, final Response response) throws Exception {
        easyID.setResponse(response);
        easyID.countDown();
    }
}
