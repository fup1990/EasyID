package com.gome.pop.fup.easyid.id;

import com.gome.pop.fup.easyid.handler.DecoderHandler;
import com.gome.pop.fup.easyid.handler.EasyHandler;
import com.gome.pop.fup.easyid.handler.EncoderHandler;
import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.model.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.util.concurrent.CountDownLatch;

/**
 * 客户端ID生成类
 * Created by fupeng-ds on 2017/8/3.
 */
public class EasyID {

    private static final Logger logger = Logger.getLogger(EasyID.class);

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    private ChannelFuture channelFuture;

    private volatile  Response response;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public EasyID() {
        startup();
    }

    /**
     * 获取id
     * @return
     */
    public long nextId() {
        return getId();
    }

    private long getId() {
        Response response = getResponse();
        return response.getId();
    }

    private synchronized Response getResponse() {
        if (connect("192.168.3.5", 9131)) {
            send();
        }
        return response;
    }


    private void startup() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch)
                            throws Exception {
                        ch.pipeline()
                                .addLast(new EncoderHandler())
                                .addLast(new DecoderHandler(Response.class))
                                .addLast(new EasyHandler(EasyID.this));
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                group.shutdownGracefully();
            }
        }));
    }

    public boolean connect(String host, int port) {
        boolean isConnect = true;
        try {
            // 链接服务器
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            isConnect = false;
        }

        return isConnect;
    }

    public void send() {
        try {
            // 将request对象写入outbundle处理后发出
            channelFuture.channel().writeAndFlush(new Request()).sync();
            countDownLatch.await();
            if (response != null && response.isFinish()) {
                // 服务器同步连接断开时,这句代码才会往下执行
                channelFuture.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void countDown() {
        this.countDownLatch.countDown();
    }
}
