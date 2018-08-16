package com.dawndevil.netty.demo1.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;


/**
 * 本例子用来研究ChannelHandlerContext和Channel的pipine流的传递方式
 *
 * channel<--->h1<---->h2<---->h3<---->h4
 *     |       ^       ^       ^       ^
 *     |-------|-------|-------|-------|
 *            ctx1    ctx2    ctx3    ctx4
 * pipeline流在整个netty里面类似于上图
 * channel连接一条线，而ctx只是关联当前的handler和下一个handler
 *
 * 当调用ctx的事件传播的时候，比如write，是从ctx对应的当前handler向前传播:
 *      比如ctx在ctx3处，那么调用write方法就会沿着   h2->h1调用
 * 但是当调用channel的write方法的时候，则是沿着整条链的头或者尾进行传播：
 *      比如ctx在ctx3处，那么调用channel的write方法，会从h4开始，h4->h3->h2->h1进行传播
 */
public class HandlerServer {
    public static void main(String[] args) {
        EventLoopGroup gserver = new NioEventLoopGroup();
        EventLoopGroup gclient = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(gserver, gclient)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new InHandler())
                                    .addLast(new OutHandler())
                                    .addLast(new OutHandler2())
                            ;

                            for(Map.Entry m: ch.pipeline()) {
                                System.out.println(m.getValue());
                            }
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 1000)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            ;
            ChannelFuture f = bootstrap.bind(8000).sync();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        System.out.println("successfully create server...");
                    }else {
                        System.out.println("failed create server...");
                    }
                }
            });
            f.channel().closeFuture().sync();
        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            gserver.shutdownGracefully();
            gclient.shutdownGracefully();
        }
    }
}
