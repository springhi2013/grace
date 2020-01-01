package com.example.helloworld

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import kotlinx.android.synthetic.main.activity_main.snapImage
import kotlinx.android.synthetic.main.activity_main.snapVideo
import kotlinx.android.synthetic.main.activity_main.album
import kotlinx.android.synthetic.main.activity_main.textureView
import com.example.helloworld.view.CameraViewManager
import android.widget.ImageView
import android.graphics.Bitmap
import com.example.helloworld.view.IRefreshBitmap
import android.widget.ImageButton


class MainActivity : AppCompatActivity() , IRefreshBitmap {
    override fun newBitmap(bitmap: Bitmap?) {
        refreSmall(bitmap)    }

    private var mCameraManager: CameraViewManager? = null

    private var mMiddleView : ImageButton? = null

    private var mBigView : ImageBuffon? = null

    private val mSmallView : ImageButton? = null
    private var abc: Int = 0

    private var name = ""

    private var age:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        mCameraManager?.setBackcall(this)
        setListener()
    }

    fun initView() {
        mCameraManager = findViewById(R.id.textureView)
    }

    fun refreSmall(bitmap:Bitmap?) {
        log("refreSmall:" + bitmap?.byteCount )
        mSmallView?.setImageBitmap(bitmap)
    }


    fun setListener() {
        snapImage.setOnClickListener(View.OnClickListener {
            textureView.startSnapImage()
        })


        snapVideo.setOnClickListener(View.OnClickListener { textureView.startSnapVideo()})

        album.setOnClickListener(View.OnClickListener { })

    }
}
