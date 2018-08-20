package com.dawndevil.netty.demo5.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class AfterProtocolHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        EasyProtocol protocol = (EasyProtocol) msg;
        System.out.println(
                "magic: " + protocol.getMagic() + ","
                + "size: " + protocol.getCount() + ","
                + "content: " + protocol.getContent()
        );
    }
}
