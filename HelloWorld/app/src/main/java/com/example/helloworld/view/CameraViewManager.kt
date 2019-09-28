package com.example.helloworld.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.*

import android.util.AttributeSet
import android.view.TextureView
import androidx.core.content.ContextCompat
import android.view.Surface
import java.util.*
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.StreamConfigurationMap
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.*
import java.lang.Exception
import android.app.Activity
import android.graphics.*
import android.media.Image
import androidx.core.app.ActivityCompat
import com.example.helloworld.MainActivity
import com.example.helloworld.MyLogUtil
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


class CameraViewManager(context: Context, attrs: AttributeSet?) : TextureView(context, attrs) {

    private var mContext: Context = context
    private var mHandler: Handler
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
    private var mSnapImageRequestBuilder: CaptureRequest.Builder? = null
    private var mSnapVideoRequestBuilder: CaptureRequest.Builder? = null
    private var mMediaRecorder: MediaRecorder? = null

    private var mBackHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    private var mSurfaceWidth: Integer? = null
    private var mSurfaceHeight: Integer? = null

    private var mVideoRoot: String? = null
    private var localPath: String? = null


    init {
        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mHandler = Handler(Looper.getMainLooper())
        this.surfaceTextureListener = SurfaceListener()
        mCameraCharacteristics = mCameraManager.getCameraCharacteristics(0.toString()) // 获取前摄像头的特征
        mVideoRoot = "/storage/sdcard0/1.mp4"
        log("path:" + mVideoRoot)
        printCameraList();
        localPath = context.filesDir.absolutePath
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
        mPreviewRequestBuilder!!.addTarget(imageReader?.surface!!)
        mCameraDevice.createCaptureSession(Arrays.asList(mPreviewSurface, imageReader?.surface), CaptureStateCb(), null)
    }

    fun startPreview() {
        if (mPreviewRequestBuilder == null) {
            log("mCaptureSession or mPreviewRequestBuilder is null");
            return
        }
        try {
            // 开始预览，即一直发送预览的请求
            mCaptureSession!!.setRepeatingRequest(this!!.mPreviewRequest!!, null, null);
        } catch (e: CameraAccessException) {
            e.printStackTrace();
        }
    }

    fun startSnapImage() {
        try {
            mSnapImageRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            mSnapImageRequestBuilder!!.addTarget(imageReader!!.surface) // 拍照时，是将ImageReader.getSurface()作为目标

            mSnapImageRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            mSnapImageRequestBuilder!!.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)

            mCaptureSession!!.stopRepeating()
            mCaptureSession!!.abortCaptures()

            mCaptureSession!!.capture(mSnapImageRequestBuilder!!.build(), CallBackSnapImage(), mBackHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun startSnapVideo() {
        try {
            mSnapVideoRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            setUpMediaRecorder()

            var videoSurface: Surface = getVideoSurface()
            mPreviewRequestBuilder!!.addTarget(videoSurface)
            mCameraDevice.createCaptureSession(Arrays.asList(videoSurface), VideoCallBack(), mBackHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var mNextVideoAbsolutePath: String? = null

    fun setUpMediaRecorder() {

        createNewFile(mVideoRoot)
        mMediaRecorder = MediaRecorder()
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mNextVideoAbsolutePath = mVideoRoot
        mMediaRecorder!!.setOutputFile(mNextVideoAbsolutePath)
        mMediaRecorder!!.setVideoEncodingBitRate(10000000)

        mMediaRecorder!!.setVideoFrameRate(30)
        mMediaRecorder!!.setVideoSize(300, 300)
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder!!.setVideoSize(mSurfaceWidth!!.toInt(), mSurfaceHeight!!.toInt())

        mMediaRecorder!!.prepare()
    }

    fun getVideoSurface(): Surface {

        val recorderSurface: Surface = mMediaRecorder!!.getSurface()

        return recorderSurface
    }


    fun openCamera(width: Integer, height: Integer) {
        try {
            val map: StreamConfigurationMap =
                mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) as StreamConfigurationMap

            imageReader = ImageReader.newInstance(width.toInt(), height.toInt(), ImageFormat.YUV_420_888, 1)
            imageReader!!.setOnImageAvailableListener(ImageReaderONImage(), mBackHandler)

            if (checkPermission(context, android.Manifest.permission.CAMERA, mContext.packageName)) {
                mCameraManager.openCamera(
                    "" + CameraCharacteristics.LENS_FACING_FRONT,
                    CallBackCameraState(),
                    mHandler
                )
            } else {
                log("Permisson Not allow OPen Camera")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    inner class SurfaceListener : SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
            log("onSurfaceTextureAvailable")
            mPreviewSurfaceTexture = surfaceTexture
            mSurfaceWidth = Integer(width)
            mSurfaceHeight = Integer(height)

            verifyStoragePermissions(mContext as MainActivity)

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
    inner class CallBackCameraState : CameraDevice.StateCallback() {
        // 当相机打开的时候调用
        override fun onOpened(device: CameraDevice) {
            mCameraDevice = device
            initPreviewRequest()
//            setUpMediaRecorder()
        }

        override fun onDisconnected(p0: CameraDevice) {
            log("CallBackCameraState--onDisconnected")
        }

        override fun onError(p0: CameraDevice, p1: Int) {
            log("CallBackCameraState--onError:" + p1)
        }
    }

    inner class ImageReaderONImage : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader?) {
            log("onImageAvailable--")
            try {
                val image = reader?.acquireNextImage() ?: return
                val w: Int = image.width
                val h: Int = image.height
                
                val i420Size: Int = w * h * 3 / 2
                val planes: Array<Image.Plane> = image.planes

                log("image--w--$w--h: $h--i420size:$i420Size--planesSize:${planes.size}")

                val remaining0: Int = planes[0].buffer.remaining()
                val remaining1: Int = planes[1].buffer.remaining()
                val remaining2: Int = planes[2].buffer.remaining()

                val pixelStride: Int = planes[2].pixelStride
                val rowOffset: Int = planes[2].rowStride
                val nv21 = ByteArray(i420Size)

                val yRawSrcBytes = ByteArray(remaining0)
                val uRawSrcBytes = ByteArray(remaining1)
                val vRawSrcBytes = ByteArray(remaining2)

                planes[0].buffer.get(yRawSrcBytes)
                planes[1].buffer.get(uRawSrcBytes)
                planes[2].buffer.get(vRawSrcBytes)

                log("pixelStride$pixelStride--width$width--remaining0--$remaining0--remaining1--$remaining1--remaining2--$remaining2")

                if (pixelStride == width) {
                    // 两者相等,说明每个YUV块紧密相连，可以直接复制
                    System.arraycopy(yRawSrcBytes, 0, nv21, 0, rowOffset * h)
                    System.arraycopy(vRawSrcBytes, 0, nv21, rowOffset * h, rowOffset * h / 2 - 1)
                } else {
                    val ySrcBytes = ByteArray(w * h)
                    val uSrcBytes = ByteArray(w * h / 2 - 1)
                    val vSrcBytes = ByteArray(w * h / 2 - 1)
                    for (row in 0..(h -1)) {
                        log("imgC---$row")
                        System.arraycopy(yRawSrcBytes, rowOffset * row, ySrcBytes, w * row, w)
                        if (0 == row % 2) {
                            if (row == h - 1) {
                                System.arraycopy(vRawSrcBytes, rowOffset * row / 2, vSrcBytes, w * row / 2, w - 1)
                            } else {
                                System.arraycopy(vRawSrcBytes, rowOffset * row / 2, vSrcBytes, w * row / 2, w)
                            }
                        }
                    } // end for

                    // yuv拷贝到一个数组里面
                    System.arraycopy(ySrcBytes, 0, nv21, 0, w * h)
                    System.arraycopy(vSrcBytes, 0, nv21, w * h, w * h / 2 - 1)
                }

                var bitmap: Bitmap = getBitmapImageFromYUV(nv21, w, h)

                image.close()

                mHandler.postDelayed(
                    { saveBitmapToLocal(bitmap, localPath + System.currentTimeMillis() + ".jpg") },
                    1 * 1000)
            } catch (e:Exception) {
                e.printStackTrace()
                log(e.toString())
            }

        }
    }

    fun saveBitmapToLocal(bitmap: Bitmap, localPath: String) {
        log("saveBitmapToLocal$localPath")
        Thread() {
            fun run() {
                val file = File(localPath)
                if (!file.exists()) {
                    val ftf = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ftf)
                    ftf.flush()
                    ftf.close()
                }
            }
        }.start()
    }


    fun getBitmapImageFromYUV(data: ByteArray, width: Int, height: Int): Bitmap {
        val yuvimage: YuvImage = YuvImage(data, ImageFormat.NV21, width, height, null)
        val baos = ByteArrayOutputStream()
        yuvimage.compressToJpeg(Rect(0, 0, width, height), 80, baos)
        val jdata: ByteArray = baos.toByteArray()
        val bitmapFactoryOptions: BitmapFactory.Options = BitmapFactory.Options()
        bitmapFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565

        return BitmapFactory.decodeByteArray(jdata, 0, jdata.size, bitmapFactoryOptions)
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

    inner class VideoCallBack : CameraCaptureSession.StateCallback() {

        override fun onConfigureFailed(p0: CameraCaptureSession) {

            log("VideoCallBack--onConfigureFailed")

        }

        override fun onConfigured(p0: CameraCaptureSession) {
            log("VideoCallBack--onConfigured")
            p0.setRepeatingRequest(mPreviewRequestBuilder!!.build(), null, mBackHandler)

        }
    }

    inner class CallBackSnapImage : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            log("onCaptureCompleted")
            super.onCaptureCompleted(session, request, result)
        }
    }

    fun updatePreview() {

    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        var permission: Integer =
            Integer(ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE))

        if (permission != Integer(PackageManager.PERMISSION_GRANTED)) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity, PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }

    }


    fun createNewFile(path: String?) {
        try {
            val file: File = File(path)
            if (!file.exists()) {
                file.createNewFile()
            }
        } catch (e: Exception) {

        }


    }


    fun log(str: String) {
        MyLogUtil.log("CameraViewManager--" + str)
    }


}



