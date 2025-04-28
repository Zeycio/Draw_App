package com.zeycio.drawapp.drawScreen.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

//draws a image [if can't  select random image ]
fun createFallbackBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.BLUE
        style = Paint.Style.FILL
    }

    canvas.drawColor(android.graphics.Color.LTGRAY)
    canvas.drawCircle(150f, 150f, 100f, paint)

    paint.color = android.graphics.Color.RED
    canvas.drawRect(50f, 50f, 100f, 100f, paint)

    paint.color = android.graphics.Color.GREEN
    canvas.drawRect(200f, 200f, 250f, 250f, paint)
    return bitmap
    //returns one circle with 2 rectangles i
}