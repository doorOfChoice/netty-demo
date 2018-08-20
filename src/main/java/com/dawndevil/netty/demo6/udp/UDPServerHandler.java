package com.dawndevil.netty.demo6.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;


public class UDPServerHandler extends ChannelInboundHandlerAdapter {
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        DatagramPacket buf = (DatagramPacket) msg;
        System.out.println(buf.content().toString(CharsetUtil.UTF_8));
    }
}
