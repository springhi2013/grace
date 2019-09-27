package com.example.helloworld

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.helloworld.opengl.BackgroundRender
import com.example.helloworld.view.CameraViewManager


class OpenGLActivity : AppCompatActivity() {

    private var mCameraManager: CameraViewManager? = null
    private var glSurfaceView: GLSurfaceView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)
        initView()
        setParams();
    }

    override fun onResume(){
        super.onResume()
        glSurfaceView?.onResume()
    }

    private fun initView() {
        glSurfaceView = findViewById(R.id.glSurfaceView)
    }

    private fun setParams() {
        // 设置GLContext 为 OpenGLES2.0
        glSurfaceView?.setEGLContextClientVersion(2)
        glSurfaceView?.setRenderer(BackgroundRender())
        // 设置渲染方式, 设置为被动渲染, 只有在调用requestRender或onResume等方法时才会渲染
        // RENCERMODE_CONTINUOUSLY 表示持续渲染
        glSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }



}
