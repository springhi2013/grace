package com.example.helloworld.kotlintest.p1

import com.example.helloworld.MyLogUtil

open class InitOrderDemo  constructor(name:String) {

    val firstProperty = "First property $name".also {

        MyLogUtil.log("First property")
    }

    init {
        MyLogUtil.log("First init block")
    }

    val secondProperty = "Second property :${name.length}".also { MyLogUtil.log("Second property") }

    init {
        MyLogUtil.log("Second init block that prints ${name.length}")
    }


    constructor(name:String, length:Int) : this(name) {
        MyLogUtil.log("Second constructor ")
    }

    open fun add(){}



}