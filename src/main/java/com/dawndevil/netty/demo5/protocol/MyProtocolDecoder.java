package com.dawndevil.netty.demo5.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;

public class MyProtocolDecoder extends LengthFieldBasedFrameDecoder {

    /**
     *
     * @param maxFrameLength        表示最大能传递的字节数，包括帧
     * @param lengthFieldOffset     长度相对于原始位置的偏移
     * @param lengthFieldLength     长度所占用的字节数
     * @param lengthAdjustment      针对于长度里面是否包含头部的长度，如果包含头部的长度，可以设置其为一个负的头部长度，这样最终会得到内容的长度
     * @param initialBytesToStrip   需要跳过的字节
     */
    public MyProtocolDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf)super.decode(ctx, in);

        if(buf == null) {
            return null;
        }

        int magic = 0;
        int size  = 0;
        EasyProtocol protocol = new EasyProtocol();
        protocol.setMagic(magic);
        protocol.setCount(size);
        protocol.setContent(buf.toString(CharsetUtil.UTF_8));
        buf.skipBytes(buf.readableBytes());

        return protocol;
    }
}
