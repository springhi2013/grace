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


class MainActivity : AppCompatActivity() {

    private var mCameraManager: CameraViewManager? = null
    private val mSmallView : ImageView ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setListener()
    }

    fun initView() {
        mCameraManager = findViewById(R.id.textureView)
    }


    fun setListener() {
        snapImage.setOnClickListener(View.OnClickListener {
            textureView.startSnapImage()
        })


        snapVideo.setOnClickListener(View.OnClickListener { textureView.startSnapVideo()})

        album.setOnClickListener(View.OnClickListener { })

    }
}
