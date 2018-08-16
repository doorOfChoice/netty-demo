package com.dawndevil.netty.demo3.jsonserver.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class ResponseOperator {
    public static final String TEXT_JSON = "text/json";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_XML = "text/xml";
    public static final String TEXT_HTML = "text/html";

    private Channel channel;

    public ResponseOperator(Channel channel) {
        this.channel = channel;
    }

    FullHttpResponse wrapResponse(ByteBuf buf, int status) {
        FullHttpResponse rep = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status), buf);
        rep.headers().set("Content-Length", buf.readableBytes());
        return rep;
    }

    public void writeResponse(ByteBuf buf, int status, String ...type) {
        FullHttpResponse rep = wrapResponse(buf, status);
        String defaultType = TEXT_JSON;
        if(type.length > 0) {
            defaultType = type[0];
        }
        rep.headers().set("Content-Type", defaultType);

        ChannelFuture f = channel.writeAndFlush(rep);
        f.addListener(ChannelFutureListener.CLOSE);
    }

    public void writeStringResponse(String str, int status, String ...type) {
        ByteBuf buf = Unpooled.copiedBuffer(str.getBytes());
        writeResponse(buf, status, type);
    }
}