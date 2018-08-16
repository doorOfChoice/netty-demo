package com.dawndevil.netty.demo3.jsonserver;

import com.dawndevil.netty.demo3.jsonserver.util.ResponseOperator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ControllerImpl implements Controller {
    public void dealAll(ByteBuf buf, HttpRequest req, ResponseOperator rep) {
        String type = req.headers().get("Content-Type");
        if(req.uri().equals("/")) {
            ByteBuf returnBuf = null;
            try {
                String path = getClass().getResource("/").getPath();
                byte[] bytes = Files.readAllBytes(Paths.get(path + "index.html"));
                returnBuf = Unpooled.copiedBuffer(bytes);
                rep.writeResponse(returnBuf, 200, ResponseOperator.TEXT_HTML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
