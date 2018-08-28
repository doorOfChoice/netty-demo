package com.dawndevil.netty.demo8.sock5.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 实现SimpleChannelInboundHandler有一个好处就是如果类型不相似，可以自动跳转到下一个Handler
 */
public class Socks5Initlizer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                .addLast(new HeartBeatHandler())
                .addLast(Socks5ServerEncoder.DEFAULT)
                .addLast(new Socks5InitialRequestDecoder())
                .addLast(new Socks5InitlizerHandler())
                .addLast(new Socks5CommandRequestDecoder())
                .addLast(new Socks5CommandHandler());
    }
}
