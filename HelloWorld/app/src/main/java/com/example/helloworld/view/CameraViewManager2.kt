package com.example.helloworld.view

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.os.Looper

import android.util.AttributeSet
import android.view.TextureView
import androidx.core.content.ContextCompat
import android.os.Handler
import android.util.Log
import android.graphics.SurfaceTexture
import android.view.Surface
import java.util.*
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.os.HandlerThread


class CameraViewManager2(context: Context, attrs: AttributeSet?) : TextureView(context, attrs) {

    private var mContext: Context = context
    /**
     * 相机相关
     * */
    private lateinit var mCameraManager: CameraManager
    private lateinit var mCameraDevice: CameraDevice
    private lateinit var mCameraCharacteristics: CameraCharacteristics
    private var mCaptureSession: CameraCaptureSession? = null
    private var mPreviewRequest: CaptureRequest? = null

    private var imageReader: ImageReader? = null

    /**
     * 显示相关
     * */
    private var mPreviewSurface: Surface? = null
    private var mPreviewSurfaceTexture: SurfaceTexture? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mRecordRequestBuilder: CaptureRequest.Builder? = null

    private var mBackHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null


    init {

        this.surfaceTextureListener = SurfaceListener()

    }

    private fun printCameraList() {
        val list: Array<String> = mCameraManager.cameraIdList
        for (item in list) {
            log(" camera: " + item)
        }
    }

    fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackHandler = Handler(mBackgroundThread!!.looper)
    }

    fun startRequestPermission(str: String): Boolean {
        return ContextCompat.checkSelfPermission(mContext, str) == PackageManager.PERMISSION_DENIED
    }

    private fun checkPermission(context: Context, permName: String, pkgName: String): Boolean {
        val pm = context.packageManager
        if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName)) {
            log(pkgName + "has permission : " + permName)
            return true
        } else {
            //PackageManager.PERMISSION_DENIED == pm.checkPermission(permName, pkgName)
            log(pkgName + "not has permission : " + permName)
            return false
        }
    }


    fun initPreviewRequest() {
        mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        if (null != mPreviewSurfaceTexture && null == mPreviewSurface) {
            mPreviewSurfaceTexture!!.setDefaultBufferSize(400, 400)
            mPreviewSurface = Surface(mPreviewSurfaceTexture)
        }
        mPreviewRequestBuilder!!.addTarget(this!!.mPreviewSurface!!)
        mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface), CaptureStateCb(), null)
    }

    fun startPreview() {
        if (mPreviewRequestBuilder == null) {
            log("mCaptureSession or mPreviewRequestBuilder is null");
            return;
        }
        try {
            // 开始预览，即一直发送预览的请求
            mCaptureSession!!.setRepeatingRequest(this!!.mPreviewRequest!!, null, null);
        } catch (e: CameraAccessException) {
            e.printStackTrace();
        }
    }

    fun getSnapRequest() {
        mRecordRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        mRecordRequestBuilder!!.addTarget(imageReader!!.surface) // 拍照时，是将ImageReader.getSurface()作为目标

        mRecordRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        mRecordRequestBuilder!!.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)

        mCaptureSession!!.stopRepeating()
        mCaptureSession!!.abortCaptures()

        mCaptureSession!!.capture(mRecordRequestBuilder!!.build(), CaptureCallback2(), null)
    }

    inner class CaptureCallback2 : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            log("onCaptureCompleted")
            super.onCaptureCompleted(session, request, result)
        }


    }


    inner class ImageReaderONImage : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(p0: ImageReader?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }


    inner class CaptureStateCb : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(p0: CameraCaptureSession) {

        }

        override fun onConfigured(p0: CameraCaptureSession) {
            mCaptureSession = p0
            mPreviewRequest = mPreviewRequestBuilder!!.build();
            startPreview()
        }
    }


    inner class SurfaceListener : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, width: Int, height: Int) {
            log("onSurfaceTextureAvailable")
            mPreviewSurfaceTexture = p0
            startBackgroundThread()
            openCamera(Integer(width), Integer(height))
        }

        override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {
            log("onSurfaceTextureDestroyed")
            return true
        }

        override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, width: Int, height: Int) {
            log("onSurfaceTextureSizeChanged--width:" + width + " height:" + height)
        }

        override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {
            log("onSurfaceTextureUpdated")
        }

    }

    /**
     * 当相机设备的状态发生改变的时候，将会回调
     * */
    inner class CameraCallBack : CameraDevice.StateCallback() {
        // 当相机打开的时候调用
        override fun onOpened(p0: CameraDevice) {
            mCameraDevice = p0
            initPreviewRequest()
        }

        override fun onDisconnected(p0: CameraDevice) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }


    fun openCamera(width: Integer, height: Integer) {
//        try {
//            val map: StreamConfigurationMap =
//                mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) as StreamConfigurationMap
//
//            imageReader = ImageReader.newInstance(width.toInt(), height.toInt(), ImageFormat.JPEG, 7)
//            imageReader!!.setOnImageAvailableListener(ImageReaderONImage(), mBackHandler)
//
//            if (checkPermission(context, android.Manifest.permission.CAMERA, mContext.packageName)) {
//                mCameraManager.openCamera("" + CameraCharacteristics.LENS_FACING_FRONT, CallBackCameraState(), mHandler)
//            }
//        } catch (e: java.lang.Exception) {
//
//        }
    }


    fun log(str: String) {
        Log.e("", "CameraViewManager--" + str)
    }


}



