package com.dawndevil.netty.demo6.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class UDPServer {
    public static void main(String[] args) {
        EventLoopGroup gserver = new NioEventLoopGroup();

        try {
            /**
             * udp 不能设置 ServerBootStrap
             */
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(gserver)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringEncoder())
                                    .addLast(new StringDecoder())
                                    .addLast(new UDPServerHandler())
                            ;
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
            ;
            ChannelFuture f = bootstrap.bind("127.0.0.1", 8000).sync();
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            gserver.shutdownGracefully();
        }
    }
}