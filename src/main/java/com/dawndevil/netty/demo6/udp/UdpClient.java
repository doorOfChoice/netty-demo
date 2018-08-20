package com.dawndevil.netty.demo6.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UdpClient {
    public static DatagramPacket warp(String msg) throws SocketException {
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8000);
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes());
        return new DatagramPacket(buf, address);
    }

    public static void main(String[] args) throws IOException {
        EventLoopGroup clients = new NioEventLoopGroup();

        try {
            /**
             * udp 不能设置 ServerBootStrap
             */
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clients)
                    .channel(NioDatagramChannel.class)
                    .handler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {

                        }
                    })
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
            ;
            ChannelFuture f = bootstrap.connect("127.0.0.1", 8000).sync();
            Channel channel = f.channel();
            Scanner scan = new Scanner(System.in);
            while(scan.hasNextLine()) {
                channel.writeAndFlush(warp(scan.nextLine()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            clients.shutdownGracefully();
        }
    }
}
