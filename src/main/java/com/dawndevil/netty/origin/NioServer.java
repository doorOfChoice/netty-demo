package com.dawndevil.netty.origin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8000));
        server.configureBlocking(false);
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {
            int keys = selector.select();

            if(keys == 0) {
                continue;
            }
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> keyIter = set.iterator();
            while(keyIter.hasNext()) {
                SelectionKey key = keyIter.next();
                try {
                    if(key.isAcceptable()) {
                        ServerSocketChannel s = (ServerSocketChannel)key.channel();
                        SocketChannel client = s.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE)
                                .attach(ByteBuffer.allocate(1024));

                    }

                    if(key.isReadable()) {
                        SocketChannel client = (SocketChannel)key.channel();
                        ByteBuffer buf = (ByteBuffer) key.attachment();
                        buf.clear();
                        int k = client.read(buf);
                        if(-1 == k) {
                            client.close();
                        }else {
                            buf.flip();
                            System.out.println(Charset.forName("UTF-8").newDecoder().decode(buf).toString());
                        }
                    }
                    keyIter.remove();
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
