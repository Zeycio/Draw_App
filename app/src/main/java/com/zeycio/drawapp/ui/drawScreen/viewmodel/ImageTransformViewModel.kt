package com.zeycio.drawapp.ui.drawScreen.viewmodel

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ImageTransformViewModel : ViewModel() {

    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> get() = _selectedBitmap

    private val _isImagePlaced = MutableStateFlow(false)
    val isImagePlaced: StateFlow<Boolean> get() = _isImagePlaced

    private val _isInDrawingMode = MutableStateFlow(false)
    val isInDrawingMode: StateFlow<Boolean> get() = _isInDrawingMode

    private val _currentColor = MutableStateFlow(android.graphics.Color.BLACK)
    val currentColor: StateFlow<Int> get() = _currentColor

    private val _brushSize = MutableStateFlow(10f)
    val brushSize: StateFlow<Float> get() = _brushSize


    fun setSelectedBitmap(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
    }

    fun setIsImagePlaced(placed: Boolean) {
        _isImagePlaced.value = placed
    }

    fun setIsInDrawingMode(enabled: Boolean) {
        _isInDrawingMode.value = enabled
    }

    fun setCurrentColor(color: Int) {
        _currentColor.value = color
    }

    fun setBrushSize(size: Float) {
        _brushSize.value = size
    }


    // for clear
    fun resetAll() {
        _brushSize.value = 10f
        _isInDrawingMode.value = false
        _selectedBitmap.value = null
        _isImagePlaced.value = false
    }

    // for draw
    fun setAll() {
        viewModelScope.launch {
            _isImagePlaced.value = true
            _isInDrawingMode.value = true
            drawToCanvas?.invoke()
            delay(100)
            drawToCanvas?.invoke()
        }
    }


    val transformMatrix = Matrix()
    var midX = 0f
    var midY = 0f


    // Current transformation values
    var translateX = 0f
    var translateY = 0f
    var scale = 1f
    var rotation = 0f


    var drawToCanvas: (() -> Unit)? = null
    var clearCanvas: (() -> Unit)? = null

    fun resetTransformation() {
        transformMatrix.reset()
        translateX = 0f
        translateY = 0f
        scale = 1f
        rotation = 0f
    }

    fun updateTransformMatrix() {
        transformMatrix.reset()
        transformMatrix.postTranslate(
            -selectedBitmap.value!!.width / 2f,
            -selectedBitmap.value!!.height / 2f
        )
        transformMatrix.postScale(scale, scale)
        transformMatrix.postRotate(rotation)
        transformMatrix.postTranslate(translateX, translateY)
    }
}