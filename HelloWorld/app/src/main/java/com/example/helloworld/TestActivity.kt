package com.example.helloworld

import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class TestActivity : AppCompatActivity() {

    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setContentView(R.layout.test)

        mHandler = Handler(Looper.getMainLooper())
        mHandler?.postDelayed({ testTrace() }, 5 * 1000)
    }


    private fun testTrace() {
        val file = getExternalFilesDir(null)
        val fileName = file?.absolutePath + "/trace2"

        Log.e("", "testTrace: $fileName" )

        // 相当于SystemTrace
        Debug.startMethodTracing(fileName)

        Thread.sleep(3 * 1000)

        Debug.stopMethodTracing()


        // 相当于TraceView
        Trace.beginSection("add")
         var j = 0
        for(i in 0..1000000){
            j++
        }
        System.out.println("$j")

        Trace.endSection()
    }


}