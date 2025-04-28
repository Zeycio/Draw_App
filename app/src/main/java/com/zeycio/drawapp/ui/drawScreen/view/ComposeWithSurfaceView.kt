package com.zeycio.drawapp.ui.drawScreen.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.zeycio.drawapp.ui.drawScreen.model.DrawingSurfaceView
import com.zeycio.drawapp.ui.drawScreen.viewmodel.ImageTransformViewModel


@Composable
fun ComposeWithSurfaceView(
    viewModel: ImageTransformViewModel, onSizeChanged: (Int, Int) -> Unit
) {
    AndroidView(
        factory = { context ->
            DrawingSurfaceView(context, viewModel).apply {
                setOnSizeChangedListener(onSizeChanged)
                viewModel.drawToCanvas = {
                    drawSelectedImageOnCanvas()
                }
                viewModel.clearCanvas = {
                    canvasBitmap?.eraseColor(android.graphics.Color.TRANSPARENT)
                    postInvalidate()
                }
            }
        }, modifier = Modifier.fillMaxSize()
    )
}
