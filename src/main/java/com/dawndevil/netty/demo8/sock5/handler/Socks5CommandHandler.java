package com.dawndevil.netty.demo8.sock5.handler;

import com.dawndevil.netty.demo8.sock5.Socks5Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5CommandHandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    private static Logger logger = LoggerFactory.getLogger(Socks5Server.class);

    private DefaultSocks5CommandRequest msg;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) throws Exception {
        logger.info("{} {} {}", msg.type(), msg.dstAddr(), msg.dstPort());
        this.msg = msg;
        if(msg.type().equals(Socks5CommandType.CONNECT)) {
            createServer(ctx, msg);
        }
    }

    private void createServer(final ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new Server2Client(ctx.channel()));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true);
        ChannelFuture f = bootstrap.connect(msg.dstAddr(), msg.dstPort());
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    Socks5CommandResponse rep = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                    ctx.pipeline().addLast(new Client2Server(future.channel()));
                    ctx.writeAndFlush(rep);
                }else {
                    Socks5CommandResponse rep = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                    ctx.writeAndFlush(rep);
                }
            }
        });
    }

    /**
     * client - mediumserver
     */
    private class Client2Server extends ChannelInboundHandlerAdapter {
        private Channel channel;

        public Client2Server(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            channel.writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.warn("{} {} 中转服务器已经关闭", msg.dstPort(), msg.dstAddr());
            channel.close();
        }
    }

    /**
     * server - mediumserver
     */
    private class Server2Client extends ChannelInboundHandlerAdapter {
        private Channel channel;

        public Server2Client(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            channel.writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.warn("{} {} 远程服务器已经关闭", msg.dstPort(), msg.dstAddr());
            ctx.close();
        }
    }
}
