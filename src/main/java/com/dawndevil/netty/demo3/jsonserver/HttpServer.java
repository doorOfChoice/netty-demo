package com.dawndevil.netty.demo3.jsonserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {
    public static void main(String[] args)  {
        EventLoopGroup gserver = new NioEventLoopGroup();
        EventLoopGroup gclient = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(gserver, gclient)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("jsonserver-chunked", new ChunkedWriteHandler())
                                    .addLast("jsonserver-codec", new HttpServerCodec())
                                    .addLast("jsonserver-aggregator", new HttpObjectAggregator(Integer.MAX_VALUE))
                                    .addLast(new HttpRequestHandler())
                            ;
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
