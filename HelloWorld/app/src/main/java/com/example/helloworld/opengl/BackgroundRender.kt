package com.example.helloworld.opengl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.example.helloworld.MyLogUtil
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class BackgroundRender : GLSurfaceView.Renderer {
    override fun onDrawFrame(p0: GL10?) {
        log("onDrawFrame")
        GLES20.glClearColor(GLES20.GL_COLOR_BUFFER_BIT.toFloat(), 0F, 0F, 0F)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        log("onSurfaceChanged--width$width--height$height")
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        log("onSurfaceCreated")
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    private fun log(msg: String) {
        MyLogUtil.log(msg)
    }
}