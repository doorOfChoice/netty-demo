package com.dawndevil.netty.demo8.sock5.handler;

import com.dawndevil.netty.demo8.sock5.Socks5Server;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5InitlizerHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
    private static Logger logger = LoggerFactory.getLogger(Socks5Server.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) throws Exception {
        if(msg.decoderResult().isFailure()) {
            logger.error("fail");
            ctx.fireChannelRead(msg);
        }else {
            if(msg.version().equals(SocksVersion.SOCKS5)) {
                Socks5InitialResponse rep = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
                ctx.channel().writeAndFlush(rep);
            }else {
                ctx.fireChannelRead(msg);
            }
        }

    }
}
