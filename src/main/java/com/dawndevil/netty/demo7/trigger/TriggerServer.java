package com.dawndevil.netty.demo7.trigger;

import com.dawndevil.netty.demo1.handler.InHandler;
import com.dawndevil.netty.demo1.handler.OutHandler;
import com.dawndevil.netty.demo1.handler.OutHandler2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TriggerServer {
    public static void main(String[] args) {
        EventLoopGroup gserver = new NioEventLoopGroup();
        EventLoopGroup gclient = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(gserver, gclient)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                .addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS))
                                .addLast(new HeartBeatHandler())
                            ;

                            for(Map.Entry m: ch.pipeline()) {
                                System.out.println(m.getValue());
                            }
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            ;
            ChannelFuture f = bootstrap.bind(8000).sync();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        System.out.println("successfully create server...");
                    }else {
                        System.out.println("failed create server...");
                    }
                }
            });
            f.channel().closeFuture().sync();
        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            gserver.shutdownGracefully();
            gclient.shutdownGracefully();
        }
    }
}
