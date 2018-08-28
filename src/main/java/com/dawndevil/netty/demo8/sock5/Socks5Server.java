package com.dawndevil.netty.demo8.sock5;

import com.dawndevil.netty.demo8.sock5.handler.Socks5Initlizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Socks5Server {
    private static Logger logger = LoggerFactory.getLogger(Socks5Server.class);

    public static void main(String[] args) {
        EventLoopGroup parent = new NioEventLoopGroup();
        EventLoopGroup child  = new NioEventLoopGroup();


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parent, child)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new Socks5Initlizer())
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            ChannelFuture f = bootstrap.bind(8000);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        logger.info("Success to connect");
                    }else {
                        logger.error("Fail to connect because: {}", future.cause());
                    }
                }
            });
            f.channel().closeFuture().sync();
        }catch (Exception ex) {
            parent.shutdownGracefully();
            child.shutdownGracefully();
        }
    }
}
