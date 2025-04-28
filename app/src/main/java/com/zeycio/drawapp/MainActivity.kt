package com.zeycio.drawapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zeycio.drawapp.drawScreen.view.MainDrawScreen
import com.zeycio.drawapp.drawScreen.viewmodel.ImageTransformViewModel
import com.zeycio.drawapp.theme.DrawAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ImageTransformViewModel()
            DrawAppTheme {
                   MainDrawScreen(viewModel =viewModel, )
            }
        }
    }
}

