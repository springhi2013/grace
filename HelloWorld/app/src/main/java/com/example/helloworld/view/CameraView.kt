package com.example.helloworld.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener;
import android.hardware.Camera
import android.hardware.camera2.CameraDevice
import android.view.Surface
import android.view.SurfaceHolder
import android.util.AttributeSet
import android.util.Log
import android.hardware.camera2.CameraManager
import com.example.helloworld.MyLogUtil


class CameraView(context: Context, attrs: AttributeSet?) : TextureView(context, attrs), SurfaceTextureListener {

    var mContext: Context

    private var mPreviewSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null

    private var mSurfaceWidth: Integer? = null
    private var mSurfaceHeight: Integer? = null

    init {
        Log.e("", "CameraView--init");
        mContext = context
        this.surfaceTextureListener = this
    }

    fun getSurface(): Surface? {
        if (null != mSurface) {
            return mSurface
        }

        if (null != mPreviewSurfaceTexture) {
            mSurface = Surface(mPreviewSurfaceTexture)
        }
        return mSurface
    }

    fun getSurfaceWidth(): Integer? {
        return mSurfaceWidth
    }

    fun getSurfaceHeight(): Integer? {
        return mSurfaceHeight
    }


    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        mPreviewSurfaceTexture = surfaceTexture
        mSurfaceWidth = Integer(width)
        mSurfaceHeight = Integer(height)
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {

    }

    fun log(str: String) {
        MyLogUtil.log("CameraView--$str")
    }

}