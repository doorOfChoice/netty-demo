package com.dawndevil.netty.demo8.sock5.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static final ByteBuf HEARTBEAT = Unpooled.copiedBuffer("idle".getBytes());


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent)evt;
            ChannelFuture f = ctx.writeAndFlush(HEARTBEAT.copy());
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()) {
                        future.channel().close();
                        System.out.println("当前客户端已经关闭");
                    }else {
                        System.out.println("成功心跳");
                    }
                }
            });
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
