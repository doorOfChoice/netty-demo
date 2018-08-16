package com.dawndevil.netty.demo4.ssl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

public class SSLServer {
    public static void main(String[] args) throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContextBuilder sslContext = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey());

        EventLoopGroup gserver = new NioEventLoopGroup();
        EventLoopGroup gclient = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(gserver, gclient)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SSLInitializer(sslContext))
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
