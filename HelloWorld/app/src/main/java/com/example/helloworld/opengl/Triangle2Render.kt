package com.example.helloworld.opengl

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Triangle2Render : GLSurfaceView.Renderer{

    private val triangleCoords = floatArrayOf(
        0.5f, 0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f,
        0.5f, -0.5f, 0.0f
    )

    private fun init() {

    }

    override fun onDrawFrame(p0: GL10?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}