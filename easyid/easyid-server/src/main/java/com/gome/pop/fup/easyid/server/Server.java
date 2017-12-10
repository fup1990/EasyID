package com.gome.pop.fup.easyid.server;

import com.gome.pop.fup.easyid.handler.DecoderHandler;
import com.gome.pop.fup.easyid.handler.EncoderHandler;
import com.gome.pop.fup.easyid.handler.Handler;
import com.gome.pop.fup.easyid.model.Request;
import com.gome.pop.fup.easyid.snowflake.Snowflake;
import com.gome.pop.fup.easyid.util.Constant;
import com.gome.pop.fup.easyid.util.IpUtil;
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

/**
 * 服务端，接收创建id的请求
 * Created by fupeng-ds on 2017/8/2.
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    private Snowflake snowflake = new Snowflake();

    private AcceptThread acceptThread;

    public void startup() {
        logger.info("Server starting");
        acceptThread = new AcceptThread();
        acceptThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Server.this.close();
            }
        }));
    }

    /**
     * 接收线程，用于接收客户端请求
     */
    private class AcceptThread extends Thread {

        private EventLoopGroup bossGroup;

        private EventLoopGroup workerGroup;

        private ServerBootstrap bootstrap;

        public AcceptThread() {
            logger.info("AcceptThread starting");
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
                                    .addLast(new EncoderHandler())
                                    .addLast(new Handler(Server.this));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        }

        @Override
        public void run() {
            try {
                String host = IpUtil.getLocalHost();
                int port = Constant.EASYID_SERVER_PORT;
                ChannelFuture channelFuture = bootstrap.bind(host, port).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                close();
            }
        }

        public void close() {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void close() {
        acceptThread.close();
    }

    public Snowflake getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(Snowflake snowflake) {
        this.snowflake = snowflake;
    }
}
