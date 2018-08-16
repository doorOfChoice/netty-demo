package com.dawndevil.netty.demo2.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 负责处理http和https隧道
 */
public class ProxyHandler extends ChannelInboundHandlerAdapter {
    //远程主机地址
    private String ip;

    //远程主机端口，默认80
    private String port = "80";

    //tls隧道
    private Channel tlsChannel;

    //http编码器是否已经删除
    //避免重复删除导致发生异常
    private boolean encoderDeleted = false;

    private Logger logger = LoggerFactory.getLogger(ProxyHandler.class);

    public ProxyHandler() {
    }

    /**
     * 处理http连接的代理
     * 先删除http-encoder，避免远程服务器返回ByteBuf却要当作HttpResponse传递给HttpResponseEncoder，这样就算返回正确结果也不会继续执行
     * 后面则建立一个BootStrap和远程主机进行连接
     * @param ctx
     * @param msg
     */
    private void dealHttpChannel(final ChannelHandlerContext ctx, final Object msg) {
        if(!encoderDeleted) {
            encoderDeleted = true;
            ctx.pipeline().remove("jsonserver-encoder");
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new ProxyProxyHandler(ctx.channel()));
                    }

                });
        ChannelFuture result = bootstrap.connect(ip, Integer.parseInt(port));
        result.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    EmbeddedChannel ec = new EmbeddedChannel();
                    ec.pipeline()
                            .addLast(new HttpRequestEncoder())
                            .addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
                    ec.writeOutbound(msg);
                    ec.finish();
                    future.channel().writeAndFlush(ec.readOutbound());
                }else {
                    future.channel().close();
                }
            }
        });
        logger.info("建立http隧道进行传输 ip: {}, port: {}", ip, port);
    }

    /**
     * 处理Https的代理
     * Https代理会首先发送一个CONNECT请求，让代理服务器和远程服务器建立连接
     * 然后再把加密后的数据经过代理服务器传递给远程服务器
     * 检测到CONNECT请求之后，需要把有关HTTP的Handler删除掉，以创建一个隧道，避免编码错误
     * @param ctx
     * @param msg
     */
    private void dealHttpsChannel(final ChannelHandlerContext ctx, final Object msg) {
        if(tlsChannel == null) {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop()) // 复用客户端连接线程池
                    .channel(ctx.channel().getClass()) // 使用NioSocketChannel来作为连接用的channel类
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            tlsChannel = ch;
                            ch.pipeline().addLast(new ProxyProxyHandler(ctx.channel()));
                        }
                    });

            ChannelFuture result = bootstrap.connect(ip, Integer.parseInt(port));
            result.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        future.channel().close();
                    }
                }
            });
        }else {
            tlsChannel.writeAndFlush(msg);
        }
        logger.info("建立https隧道进行传输 ip: {}, port: {}", ip, port);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            FullHttpRequest req;
            req = (FullHttpRequest) msg;
            String socket = req.headers().get("host");
            if(socket == null) {
                return;
            }
            if(socket.contains(":")) {
                ip = socket.split(":")[0];
                port = socket.split(":")[1];
            }else {
                ip = socket;
                port = "80";
            }
            logger.info("发现一个https的连接 ip: {}, port: {}", ip, port);
            //如果method为CONNECT，说明是一个https请求，则返回200，并且去除所有有关http的handler，配置为一个隧道
            if(req.method().equals(HttpMethod.CONNECT)) {
                FullHttpResponse rep = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                rep.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
                ctx.writeAndFlush(rep);
                if(!encoderDeleted)
                    ctx.pipeline().remove("jsonserver-encoder");
                ctx.pipeline().remove("jsonserver-decoder");
                ctx.pipeline().remove("jsonserver-aggregator");
                encoderDeleted = true;
                return;
            }

            dealHttpChannel(ctx, msg);
        } else {
            dealHttpsChannel(ctx, msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("error: {}", cause);
    }
}
