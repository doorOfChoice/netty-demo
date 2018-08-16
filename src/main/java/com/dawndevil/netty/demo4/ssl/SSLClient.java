package com.dawndevil.netty.demo4.ssl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.util.Scanner;

public class SSLClient {
    public static void main(String[] args) throws SSLException, InterruptedException {
        final SslContext sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new SSLClientInitlizer(sslCtx));

        Channel channel = bootstrap.connect("127.0.0.1", 8000).sync().channel();
        Scanner scan = new Scanner(System.in);
        for(;;) {
            channel.writeAndFlush(scan.nextLine() + "\n");
        }
    }
}
