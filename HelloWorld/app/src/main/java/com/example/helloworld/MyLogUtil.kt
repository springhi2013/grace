package com.example.helloworld

import android.util.Log

object MyLogUtil {

    fun log(msg: String){
        Log.e("", "SuperCamera--$msg")
    }
}