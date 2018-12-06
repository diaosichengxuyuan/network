package com.diaosichengxuyuan.network.nio.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * NIO客户端
 *
 * 1.Selector.selectedKeys这个集合对象一直是同一个，每次变化的是集合中的SelectionKey个数
 * 2.SelectionKey对象不会变(因为Selector使用SelectionKey[]缓存了所有的SelectionKey)
 * 3.SelectionKey中持有SocketChannel，如果连接不关闭，SocketChannel一直是同一个，每次变化的是注册到Selector中的状态
 * 4.selector.select()方法必不可少
 * 5.SocketChannel.finishConnect()必不可少
 * 6.SocketChannel.read(buffer)没有可读数据时返回0而不是-1
 *
 * @author liuhaipeng
 * @date 2018/11/30
 */
public class NioClient1 {
    public static void main(String[] args) {
        new NioClient1().start();
    }

    public void start() {
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(4321));
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

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
                        SocketChannel channel = (SocketChannel)selectionKey.channel();
                        channel.register(selector, SelectionKey.OP_READ);

                        System.out.println("连接就绪，发送数据");

                        while(!channel.finishConnect()) {
                            TimeUnit.SECONDS.sleep(3);
                            System.out.println("连接未就绪");
                        }

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.put("我是客户端1".getBytes("UTF-8"));
                        buffer.flip();
                        channel.write(buffer);
                    } else if(selectionKey.isAcceptable()) {

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
                        buffer.put("我是客户端1".getBytes("UTF-8"));
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
