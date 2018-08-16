package com.dawndevil.netty.demo2.proxy;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 为远程服务器设立的Handler，主要用于把远程服务器返回的数据传给浏览器
 */
public class ProxyProxyHandler extends ChannelInboundHandlerAdapter {
    //需要返还给的channel
    private Channel backChannel;

    private Logger logger = LoggerFactory.getLogger(ProxyProxyHandler.class);

    public ProxyProxyHandler(Channel backChannel) {
        this.backChannel = backChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        backChannel.writeAndFlush(msg);
    }
}
