package com.example.helloworld.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtil {

    public static IntBuffer fBuffer(int[] a) {

        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);

        // 数组排列用nativeOrder

        mbb.order(ByteOrder.nativeOrder());

        IntBuffer floatBuffer = mbb.asIntBuffer();

        floatBuffer.put(a);

        floatBuffer.position(0);

        return floatBuffer;

    }

    public static FloatBuffer fBuffer(float[] a) {
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        // 数组排列用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mbb.asFloatBuffer();
        floatBuffer.put(a);
        floatBuffer.position(0);
        return floatBuffer;
    }

}
