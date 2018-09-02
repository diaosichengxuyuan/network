package com.diaosichengxuyuan.network.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 客户端启动
 *
 * @author liuhaipeng
 * @date 2018/8/31
 */
public class Client {

    public static void main(String[] args) {
        new Client().start();
    }

    private void start() {
        //worker负责读写数据
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            //辅助启动类
            Bootstrap bootstrap = new Bootstrap();
            //设置线程池
            bootstrap.group(worker);
            //设置socket工厂
            bootstrap.channel(NioSocketChannel.class);
            //设置管道
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //心跳类
                    pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                    //处理类
                    pipeline.addLast(new ClientHandler());
                }
            });
            //增加客户端连接超时
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1);
            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8866)).sync();
            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的退出，释放NIO线程组
            worker.shutdownGracefully();
        }
    }

}

class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        if(!msg.isReadable()) {
            return;
        }

        byte[] msgArray = new byte[msg.readableBytes()];
        msg.readBytes(msgArray);
        String msgString = new String(msgArray, "UTF-8");
        System.out.println("server response ： " + msgString);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = ByteBufAllocator.DEFAULT.buffer(100);
        msg.writeBytes("i am client !".getBytes("UTF-8"));
        ctx.channel().writeAndFlush(msg);
        System.out.println("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭管道
        ctx.channel().close();
        //打印异常信息
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("client handle event:" + evt);
    }
}
