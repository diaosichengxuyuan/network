package com.diaosichengxuyuan.network.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 服务端启动
 *
 * @author liuhaipeng
 * @date 2018/8/31
 */
public class Server {

    public static void main(String[] args) {
        new Server().start();
    }

    private void start() {
        //boss线程监听端口，worker线程负责数据读写
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置线程池
            bootstrap.group(boss, worker);
            //设置socket工厂
            bootstrap.channel(NioServerSocketChannel.class);
            //设置管道工厂
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //心跳类
                    pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                    //处理类
                    pipeline.addLast(new ServerHandler());
                }
            });
            //设置TCP参数
            //1.链接缓冲池的大小（ServerSocketChannel的设置）
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //维持链接的活跃，清除死链接(SocketChannel的设置)
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //关闭延迟发送
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            //绑定端口
            ChannelFuture future = bootstrap.bind(8866).sync();
            System.out.println("server start ...... ");
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放线程池资源
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            if(!msg.isReadable()) {
                System.out.println("no data to read.");
                return;
            }

            byte[] msgArray = new byte[msg.readableBytes()];
            msg.readBytes(msgArray);
            String msgString = new String(msgArray, "UTF-8");
            System.out.println("client response :" + msgString);

            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(100);
            byteBuf.writeBytes("i am server !".getBytes("UTF-8"));
            ctx.channel().writeAndFlush(byteBuf);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelActive");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channelInactive");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //关闭通道
            ctx.channel().close();
            //打印异常
            cause.printStackTrace();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            System.out.println("server handle event:" + evt);
        }
    }

}
