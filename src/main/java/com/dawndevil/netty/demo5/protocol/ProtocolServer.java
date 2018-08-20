package com.dawndevil.netty.demo5.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ProtocolServer {
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
                                    .addLast(new MyProtocolDecoder(65535, 4, 4, 0, 8))
                                    .addLast(new AfterProtocolHandler())
                            ;
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            ;
            ChannelFuture f = bootstrap.bind(8000).sync();
            f.channel().closeFuture().sync();
        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            gserver.shutdownGracefully();
            gclient.shutdownGracefully();
        }
    }
}
