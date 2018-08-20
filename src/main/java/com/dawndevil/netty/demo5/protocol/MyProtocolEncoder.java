package com.dawndevil.netty.demo5.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyProtocolEncoder extends MessageToByteEncoder<EasyProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, EasyProtocol msg, ByteBuf out) throws Exception {
        if(msg == null) {
            return;
        }

        out.writeInt(msg.getMagic());
        out.writeInt(msg.getCount());
        out.writeBytes(msg.getContent().getBytes());
    }
}
