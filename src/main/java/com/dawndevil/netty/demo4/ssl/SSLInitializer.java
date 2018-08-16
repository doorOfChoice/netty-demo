package com.dawndevil.netty.demo4.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

public class SSLInitializer extends ChannelInitializer {
    private final SslContextBuilder sslCtx;

    public SSLInitializer(SslContextBuilder sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(sslCtx.build().newHandler(ch.alloc()))
                .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(new SSLServerHandler())
        ;
    }
}
