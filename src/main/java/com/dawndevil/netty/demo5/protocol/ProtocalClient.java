package com.dawndevil.netty.demo5.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class ProtocalClient {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new MyProtocolEncoder());

        Channel channel = bootstrap.connect("127.0.0.1", 8000).sync().channel();
        Scanner scan = new Scanner(System.in);
        for(;;) {
            System.out.println("请输入你的想法：");
            EasyProtocol protocol = new EasyProtocol();
            protocol.setContent(scan.nextLine() + "\n");
            protocol.setCount(protocol.getContent().getBytes().length);
            protocol.setMagic(10086);
            channel.writeAndFlush(protocol);
        }
    }
}
