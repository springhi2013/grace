package com.example.helloworld.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class TriangleRender : GLSurfaceView.Renderer {

    private var mProgram: Int? = null
    private var vertexBuffer: FloatBuffer? = null

    private var vertexShaderCode = "attribute vec4 vPosition;" + "void main() {" + " gl_Position = vPosition;" + "}"

    private var fragmentShaderCode =
        "precision mediump float;" + "uniform vec4 vColor;" + "void main() {" + " gl_FlagColor = vColor;" + "}"

    private val triangleCoords = floatArrayOf(
        0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    // 设置颜色 ，依次为 红绿蓝 和透明通道
    private val color = floatArrayOf(1.0f, 0f, 0f, 1.0f)

    val COORDS_PER_VERTEX = 3

    var mPositionHandle: Int? = null
    var mColorHandle: Int? = null

    // 顶点个数

    val vertexCount = triangleCoords.size
    val vertexStride = COORDS_PER_VERTEX * 4 // 每个顶点四个字节

    var mMatrixHandler: Int? = null

    val index = shortArrayOf(0, 1, 2, 0, 2, 3)


    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        // 将背景色设置为灰色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        // 申请底层空间
        val bb = ByteBuffer.allocateDirect(triangleCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        // 将坐标数据转换为FloatBuffer, 用以传入OpenGL ES程序
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer?.put(triangleCoords)
        vertexBuffer?.position(0)

        val vertexShader: Int = 0//loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = 0//loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram()
        // 将顶点着色器加入到程序
        GLES20.glAttachShader(mProgram!!, vertexShader)
        // 将片元着色器加入到程序中
        GLES20.glAttachShader(mProgram!!, fragmentShader)

        GLES20.glLinkProgram(mProgram!!)
    }

    override fun onDrawFrame(p0: GL10?) {
        //将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram!!)

        //获取顶点着色器的vPosition成员句柄
        mPositionHandle = GLES20.glGetAttribLocation(mProgram!!, "vPosition")
        //启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandle!!)
        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(
            mPositionHandle!!, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            vertexStride, vertexBuffer
        );
        //获取片元着色器的vColor成员的句柄
        mColorHandle = GLES20.glGetUniformLocation(mProgram!!, "vColor")
        //设置绘制三角形的颜色
        GLES20.glUniform4fv(mColorHandle!!, 1, color, 0)
        //绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(mPositionHandle!!)

    }


}