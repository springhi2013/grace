package com.example.helloworld.kotlintest.p1

interface AbcSink<in T> {

    fun setData(t: T)
}