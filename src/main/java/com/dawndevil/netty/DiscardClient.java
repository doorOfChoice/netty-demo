package com.dawndevil.netty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class DiscardClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        final Socket socket = new Socket("localhost", 8000);
        socket.getOutputStream().write("==========这句话来自客户端==========".getBytes());
        socket.getOutputStream().flush();
        socket.getOutputStream().write("\r\n".getBytes());
        socket.getOutputStream().flush();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(;;) {
                        InputStreamReader r = new InputStreamReader(socket.getInputStream());
                        char[] buf = new char[1024];
                        int k = -1;
                        while((k = r.read(buf)) != -1) {
                            System.out.println(new String(buf, 0, k));
                            Thread.sleep(3000);
                        }
                    }
                }catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        for(;;) {
            OutputStream outputStream = socket.getOutputStream();
            Scanner scan = new Scanner(System.in);
            outputStream.write(scan.nextLine().getBytes());
            outputStream.flush();
        }
    }
}
