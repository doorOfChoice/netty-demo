package com.dawndevil.netty.demo3.jsonserver;

import com.dawndevil.netty.demo3.jsonserver.util.ResponseOperator;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpRequest;

public interface Controller {
    void dealAll(ByteBuf buf, HttpRequest req, ResponseOperator rep);
}
