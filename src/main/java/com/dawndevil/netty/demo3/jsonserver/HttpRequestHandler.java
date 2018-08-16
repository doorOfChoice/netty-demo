package com.dawndevil.netty.demo3.jsonserver;

import com.alibaba.fastjson.JSONObject;
import com.dawndevil.netty.demo3.jsonserver.util.ResponseOperator;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import java.io.RandomAccessFile;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().remoteAddress();
        System.out.println("connecting.....");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest req = (FullHttpRequest) msg;

        ByteBuf buf = req.content();
        Controller controller = new ControllerImpl();
        ResponseOperator repOps = new ResponseOperator(ctx.channel());
         try {
            String type = req.headers().get("Content-Type");
            controller.dealAll(buf, req, repOps);
        } finally {
            req.release();
        }

        String path = getClass().getResource("/").getPath();
        RandomAccessFile file = new RandomAccessFile(path + "index.html", "r");

        FullHttpResponse rep = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(200));
        rep.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html");
        rep.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
        if(HttpUtil.isKeepAlive(req)) {
            rep.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(rep);
        System.out.println(rep.refCnt());




        ChannelFuture sendFuture;
        sendFuture = ctx.write(new ChunkedFile(file, 0, file.length(), 8192), ctx.newProgressivePromise());
        sendFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                System.out.println("I'm sending now ....." + progress + "...." + total);
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                if(future.isDone()) {
                    System.out.println("ok");
                }
            }
        });

        ChannelFuture last = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!HttpUtil.isKeepAlive(req)) {
            last.addListener(ChannelFutureListener.CLOSE);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("bye bye");
    }


    JSONObject dealJsonObject(ByteBuf buf) {
        JSONObject json = null;
        try {
            json = JSONObject.parseObject(buf.toString(CharsetUtil.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }


}
