import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleStateHandler;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

public class Test1 {
//    @Test
    public void testCommon() throws IOException {
        FileInputStream fin = new FileInputStream("in.txt");
        FileOutputStream fout = new FileOutputStream("out.txt");
        try {
            byte[] bytes = new byte[409800];
            int k = -1;
            while ((k = fin.read(bytes)) != -1) {
                fout.write(bytes, 0, k);
            }
        }finally {
            fout.close();
            fin.close();
        }

    }


    /**
     * MappedByteBuffer 速度贼快
     * @throws Exception
     */
    @Test
    public void testMapped() throws Exception {
        String filein =  "test.txt";
        String fileout = "test.txt";

        FileChannel fin = FileChannel.open(Paths.get(filein), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);
        FileChannel fout = FileChannel.open(Paths.get(fileout), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);

        MappedByteBuffer mbuf = fin.map(FileChannel.MapMode.READ_WRITE, 0, 100);
        assert mbuf != null;

        MappedByteBuffer mbufw = fout.map(FileChannel.MapMode.READ_WRITE, 0, 100);
        assert mbufw != null;

        mbuf.put("hahaha".getBytes());
        mbuf.force();
        byte[] bufs = new byte[100];
        mbufw.get(bufs);
        System.out.println(new String(bufs));
    }

    @Test
    public void testChannel() throws IOException {
        FileChannel cin = null;
        FileChannel cout = null;
        try {
            cin = FileChannel.open(Paths.get("in.txt"), StandardOpenOption.READ);
            cout = FileChannel.open(Paths.get("out2.txt"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
            cin.transferTo(0, cin.size(), cout);
        }finally {
            if(cout != null)
                cout.close();
            if(cin != null)
                cin.close();
        }


    }

    @Test
    public void testBuffer() {
        ByteBuf buf = Unpooled.buffer(4);
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        buf.readByte();
        buf.writeByte(5);
        for(int i = 0; i < 4; i++) {
            System.out.println(buf.readByte());
        }
        buf.resetReaderIndex();
        System.out.println(buf);
    }

//    @Test
    public void testOrigin() {
        int count =  1024;
        try {
            //=====================channel=======================
            FileChannel frc = new RandomAccessFile("in.txt", "r").getChannel();
            FileChannel fwc = new RandomAccessFile("out.txt", "rw").getChannel();
            ByteBuffer buf = ByteBuffer.allocate(count);
            long t1 = currentTimeMillis();
            while( frc.read(buf) != -1){
                buf.flip();
                fwc.write(buf);
                buf.clear();
            }
            long t2 = currentTimeMillis();
            fwc.close();
            out.println(t2 - t1);
            //====================Bio======================
            FileInputStream fs = new FileInputStream("in.txt");
            FileOutputStream fw = new FileOutputStream("out2.txt");
            byte[] buf2 = new byte[count];
            int k = -1;
            long t3 = currentTimeMillis();
            while ((k = fs.read(buf2)) != -1) {
                fw.write(buf2, 0, k);
                fw.flush();
            }
            fw.close();
            long t4 = currentTimeMillis();

            out.println(t4 - t3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testByteBuf() {
        ByteBuf buf1 = Unpooled.copiedBuffer(new byte[]{0,0,0,1});
        ByteBuf buf2 = Unpooled.copiedBuffer(new byte[]{0,0,0,1});
        CompositeByteBuf all = Unpooled.compositeBuffer();
        all.addComponents( buf1, buf2);

        buf1.release();
        System.out.println(all.readInt());
        System.out.println(all.readInt());
    }

    @Test
    public void testEventLoop() throws ExecutionException, InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);
        final ScheduledFuture<String> future = executor.schedule(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "hello world";
            }
        }, 100, TimeUnit.SECONDS);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.cancel(false);
            }
        }).start();
        System.out.println(future.get());
    }

    public static void main(String[] args) throws IOException {
//        FileChannel fin = FileChannel.open(Paths.get("in.txt"), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);
//        FileChannel fout = FileChannel.open(Paths.get("out.txt"), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE);
//
////        MappedByteBuffer mbuf = fin.map(FileChannel.MapMode.READ_WRITE, 0, fin.size());
////        assert mbuf != null;
//
////        MappedByteBuffer mbufw = fout.map(FileChannel.MapMode.READ_WRITE, 0, fin.size());
//        MappedByteBuffer mbufw = fout.map(FileChannel.MapMode.READ_WRITE, 0, 4097);
//        assert mbufw != null;
//
//        int x = mbufw.get(4098);
    }
}
