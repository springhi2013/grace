package com.example.helloworld.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint

class MyView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint: Paint = Paint()
        paint.color = Color.BLACK

        canvas?.drawText("Hello World", 0.0F, 0.0F, paint)
    }
}