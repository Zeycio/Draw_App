package com.zeycio.drawapp.ui.theme.scree.draw.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.zeycio.drawapp.ui.theme.scree.draw.model.getRandomImageFromStorage
import com.zeycio.drawapp.ui.theme.scree.draw.viewmodel.ImageTransformViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: ImageTransformViewModel, modifier: Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }
    val isInDrawingMode = viewModel.isInDrawingMode.collectAsState()
    val brushSize = viewModel.brushSize.collectAsState()
    val brushColor = viewModel.currentColor.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            ComposeWithSurfaceView(viewModel = viewModel, onSizeChanged = { width, height ->
                screenWidth = width
                screenHeight = height
            })
        }
        if (isInDrawingMode.value) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                listOf(
                    Color.Black, Color.Red, Color.Green, Color.Blue, Color.Magenta
                ).forEach { color ->
                    Box(modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .background(color, shape = CircleShape)
                        .clickable {
                            viewModel.setCurrentColor(color.toArgb())
                        })
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("Brush Size")
                Slider(
                    value = brushSize.value,
                    onValueChange = { viewModel.setBrushSize(it) },
                    valueRange = 5f..50f,
                    colors = SliderDefaults.colors(activeTrackColor = Color(brushColor.value)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val randomImage = getRandomImageFromStorage(context)
                    if (randomImage != null) {
                        viewModel.setSelectedBitmap(randomImage)
                        viewModel.setIsImagePlaced(false)
                        viewModel.setIsInDrawingMode(false)
                        viewModel.resetTransformation()
                        viewModel.translateX = screenWidth / 2f
                        viewModel.translateY = screenHeight / 2f
                        viewModel.updateTransformMatrix()
                    }
                }
            }) {
                Text("Select Image")
            }
            Button(
                onClick = {
                    viewModel.setAll()
                }, enabled = viewModel.selectedBitmap != null
            ) {
                Text("Draw")
            }
            Button(onClick = {
                viewModel.clearCanvas?.invoke()
                viewModel.resetAll()
                viewModel.resetTransformation()
            }) {
                Text("Clear")
            }
        }
    }
}
