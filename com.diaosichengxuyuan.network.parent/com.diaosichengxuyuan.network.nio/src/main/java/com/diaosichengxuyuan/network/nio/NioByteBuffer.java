package com.diaosichengxuyuan.network.nio;

import java.nio.ByteBuffer;

/**
 * java.nio.ByteBuffer
 *
 * 1.flip()只能由写模式切换读模式
 * 2.clear()和compact()可以切换为写模式
 *
 * @author liuhaipeng
 * @date 2018/11/30
 */
public class NioByteBuffer {

    public static void main(String[] args) throws Exception {
        String s1 = "liuhaipeng";
        String s2 = "niubi";

        //position=0 limit=15 capacity=15
        ByteBuffer buffer = ByteBuffer.allocate(15);
        //position=10 limit=15 capacity=15
        buffer.put(s1.getBytes("UTF-8"));
        //position=15 limit=15 capacity=15
        buffer.put(s2.getBytes("UTF-8"));

        try {
            //放入的数据length=5 > remaining=limit-position，报错
            buffer.put(s2.getBytes("UTF-8"));
        } catch(Exception e) {
        }

        //切换写模式(本来就是写模式)，position=0 limit=15 capacity=15
        buffer.clear();

        //position=10 limit=15 capacity=15
        buffer.put(s1.getBytes("UTF-8"));

        //切换读模式，position=0 limit=10 capacity=15
        buffer.flip();

        byte[] bytes = new byte[3];
        //position=3 limit=10 capacity=15
        buffer.get(bytes);
        System.out.println(new String(bytes, "UTF-8"));

        //切换写模式，position=7 limit=15 capacity=15
        buffer.compact();

        //position=12 limit=15 capacity=15
        buffer.put(s2.getBytes("UTF-8"));

        //切换读模式，position=0 limit=12 capacity=15
        buffer.flip();

        bytes = new byte[buffer.remaining()];
        //position=12 limit=12 capacity=15
        buffer.get(bytes);
        System.out.println(new String(bytes, "UTF-8"));
    }
}
