package com.example.helloworld.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView.Renderer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PointRender: Renderer {

    val mArrayS = floatArrayOf(-0.6f , 0.6f , 0f,

        -0.2f , 0f , 0f ,

        0.2f , 0.6f , 0f ,

        0.6f , 0f , 0f)


    override fun onDrawFrame(p0: GL10?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 4)

    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
    }
}