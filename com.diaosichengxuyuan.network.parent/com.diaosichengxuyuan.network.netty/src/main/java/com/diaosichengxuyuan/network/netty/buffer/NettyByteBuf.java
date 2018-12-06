package com.diaosichengxuyuan.network.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * +-------------------+------------------+------------------+
 * | discardable bytes |  readable bytes  |  writable bytes  |
 * |                   |     (CONTENT)    |                  |
 * +-------------------+------------------+------------------+
 * |                   |                  |                  |
 * 0      <=      readerIndex   <=   writerIndex    <=    capacity
 *
 *
 * 按内存分为：HeadBuffer(分配到堆内存上)/DirectBuffer(分配到直接缓冲区中)、CompositeBuffer(复合)
 * 按缓冲池分为：unpooled(无缓冲池)和pooled(有缓冲池)
 *
 * PooledByteBufAllocator.DEFAULT
 * UnpooledByteBufAllocator.DEFAULT或者Unpooled
 *
 * @author liuhaipeng
 * @date 2018/12/6
 */
public class NettyByteBuf {

    public static void main(String[] args) {
        //初始化10bytes，但是可以增长到Integer.MAX_VALUE
        ByteBuf byteBuf = Unpooled.buffer(10);
        System.out.println("初始化10字节大小的ByteBuf：" + printByteBuf(byteBuf));

        System.out.println();
        System.out.println("写float 20.5：" + printByteBuf(byteBuf.writeFloat(20.5F)));

        System.out.println();
        System.out.println("写boolean true：" + printByteBuf(byteBuf.writeBoolean(true)));

        System.out.println();
        System.out.println("写short 12：" + printByteBuf(byteBuf.writeShort(12)));

        System.out.println();
        System.out.println("写float 60.8：" + printByteBuf(byteBuf.writeFloat(60.8F)));

        System.out.println();
        System.out.println("读float：" + byteBuf.readFloat() + " " + printByteBuf(byteBuf));
        System.out.println("获取float：" + byteBuf.getFloat(0) + " " + printByteBuf(byteBuf));

        System.out.println();
        System.out.println("读boolean：" + byteBuf.readBoolean() + " " + printByteBuf(byteBuf));
        System.out.println("获取boolean：" + byteBuf.getBoolean(4) + " " + printByteBuf(byteBuf));

        System.out.println();
        System.out.println("是否可读：" + byteBuf.isReadable() + " " + printByteBuf(byteBuf));

        System.out.println();
        System.out.println("丢弃已读字节：" + printByteBuf(byteBuf.discardReadBytes()));

        System.out.println();
        System.out.println("清空：" + printByteBuf(byteBuf.clear()));
    }

    private static String printByteBuf(ByteBuf byteBuf) {
        return "[ridx:" + byteBuf.readerIndex() + ",widx:" + byteBuf.writerIndex() + ",cap:" + byteBuf.capacity() + "]";
    }

}
