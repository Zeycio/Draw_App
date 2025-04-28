package com.zeycio.drawapp.ui.theme.scree.draw.model


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.zeycio.drawapp.ui.theme.scree.draw.viewmodel.ImageTransformViewModel
import kotlin.math.atan2
import kotlin.math.sqrt


class DrawingSurfaceView(
    context: Context,
    private val viewModel: ImageTransformViewModel
) : SurfaceView(context), SurfaceHolder.Callback {

    private var drawingThread: DrawingThread? = null
    var canvasBitmap: Bitmap? = null
    private val paint = Paint().apply {
        isAntiAlias = true
    }
    private val bitmapPaint = Paint(Paint.DITHER_FLAG)
    private var surfaceWidth = 0
    private var surfaceHeight = 0

    // For touch handling
    private var mode = NONE
    private var oldX = 0f
    private var oldY = 0f
    private var oldDist = 0f
    private var oldRotation = 0f
    private val touchRegion = RectF()

    private var sizeChangedListener: ((Int, Int) -> Unit)? = null
    private val path = android.graphics.Path()
    private val pathPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }

    init {
        holder.addCallback(this)
        setZOrderOnTop(false)
        holder.setFormat(android.graphics.PixelFormat.RGBA_8888)
    }

    fun setOnSizeChangedListener(listener: (Int, Int) -> Unit) {
        sizeChangedListener = listener
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingThread = DrawingThread(holder, this)
        drawingThread?.setRunning(true)
        drawingThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        if (canvasBitmap == null || canvasBitmap?.width != width || canvasBitmap?.height != height) {
            canvasBitmap?.recycle()
            canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            clearCanvas()
        }

        sizeChangedListener?.invoke(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        drawingThread?.setRunning(false)
        while (retry) {
            try {
                drawingThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        canvasBitmap?.recycle()
        canvasBitmap = null
    }

    private fun clearCanvas() {
        val canvas = Canvas(canvasBitmap!!)
        canvas.drawColor(android.graphics.Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    fun drawSelectedImageOnCanvas() {
        viewModel.selectedBitmap.value?.let { bitmap ->
            canvasBitmap?.let { canvasBmp ->
                val canvas = Canvas(canvasBmp)
                val matrix = viewModel.transformMatrix
                canvas.drawBitmap(bitmap, matrix, bitmapPaint)
                invalidate()
            }
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (viewModel.isInDrawingMode.value) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(event.x, event.y)
                }
                MotionEvent.ACTION_MOVE -> {
                    path.lineTo(event.x, event.y)
                    val canvas = Canvas(canvasBitmap!!)
                    pathPaint.color = viewModel.currentColor.value
                    pathPaint.strokeWidth = viewModel.brushSize.value
                    canvas.drawPath(path, pathPaint)
                    postInvalidate()
                }
                MotionEvent.ACTION_UP -> {
                    path.reset()
                }
            }
            return true
        }

        if (viewModel.selectedBitmap.value == null || viewModel.isImagePlaced.value) {
            return true
        }


        val bitmap = viewModel.selectedBitmap.value ?: return true

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mode = DRAG
                oldX = event.x
                oldY = event.y

                // Calculate if touch is within the bitmap bounds
                touchRegion.set(
                    viewModel.translateX - bitmap.width * viewModel.scale / 2,
                    viewModel.translateY - bitmap.height * viewModel.scale / 2,
                    viewModel.translateX + bitmap.width * viewModel.scale / 2,
                    viewModel.translateY + bitmap.height * viewModel.scale / 2
                )
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount >= 2) {
                    mode = ZOOM
                    oldDist = spacing(event)
                    oldRotation = rotation(event)
                    midPoint(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (mode == DRAG) {
                    val dx = event.x - oldX
                    val dy = event.y - oldY
                    viewModel.translateX += dx
                    viewModel.translateY += dy
                    oldX = event.x
                    oldY = event.y
                    viewModel.updateTransformMatrix()
                    invalidate()
                } else if (mode == ZOOM && event.pointerCount >= 2) {

                    // Handle zoom
                    val newDist = spacing(event)
                    if (newDist > 10f) {
                        val scaleFactor = newDist / oldDist
                        viewModel.scale *= scaleFactor
                        oldDist = newDist
                    }

                    // Handle rotation
                    val newRotation = rotation(event)
                    viewModel.rotation += (newRotation - oldRotation)
                    oldRotation = newRotation

                    viewModel.updateTransformMatrix()
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
            }
        }

        return true
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt(x * x + y * y)
    }

    private fun rotation(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return (Math.toDegrees(atan2(y.toDouble(), x.toDouble())) % 360).toFloat()
    }

    private fun midPoint(event: MotionEvent) {
        viewModel.midX = (event.getX(0) + event.getX(1)) / 2
        viewModel.midY = (event.getY(0) + event.getY(1)) / 2
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawColor(android.graphics.Color.WHITE)
        canvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, bitmapPaint)
        }

        // Only draw the image as transform preview if not finalized
        if (viewModel.selectedBitmap.value != null && !viewModel.isImagePlaced.value) {
            canvas.drawBitmap(viewModel.selectedBitmap.value!!, viewModel.transformMatrix, paint)
        }
    }

    class DrawingThread(
        private val surfaceHolder: SurfaceHolder,
        private val drawingSurfaceView: DrawingSurfaceView
    ) : Thread() {
        private var running = false

        fun setRunning(isRunning: Boolean) {
            running = isRunning
        }

        override fun run() {
            var canvas: Canvas? = null

            while (running) {
                try {
                    canvas = surfaceHolder.lockCanvas()
                    synchronized(surfaceHolder) {
                        if (canvas != null) {
                            drawingSurfaceView.draw(canvas)
                        }
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }
    }
}
