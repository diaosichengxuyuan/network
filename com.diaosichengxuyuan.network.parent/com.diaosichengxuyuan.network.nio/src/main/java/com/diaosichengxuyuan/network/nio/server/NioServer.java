package com.diaosichengxuyuan.network.nio.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * NIO服务端
 *
 * @author liuhaipeng
 * @date 2018/11/30
 */
public class NioServer {
    public static void main(String[] args) {
        new NioServer().start();
    }

    public void start() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(4321));
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while(true) {
                int readChannels = selector.selectNow();
                if(readChannels == 0) {
                    TimeUnit.SECONDS.sleep(3);
                    continue;
                }

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if(selectionKey.isConnectable()) {

                    } else if(selectionKey.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel)selectionKey.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("接收就绪");
                    } else if(selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel)selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        while(channel.read(buffer) != 0) {

                        }
                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        System.out.println("时间" + System.currentTimeMillis() + " 读取到：" + new String(bytes, "UTF-8"));

                        channel.register(selector, SelectionKey.OP_WRITE);
                    } else if(selectionKey.isWritable()) {
                        SocketChannel channel = (SocketChannel)selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put("我是服务端".getBytes("UTF-8"));
                        buffer.flip();
                        channel.write(buffer);
                        System.out.println("写数据");
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
