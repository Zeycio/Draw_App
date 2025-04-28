package com.zeycio.drawapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zeycio.drawapp.ui.drawScreen.view.MainScreen
import com.zeycio.drawapp.ui.drawScreen.viewmodel.ImageTransformViewModel
import com.zeycio.drawapp.ui.theme.DrawAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ImageTransformViewModel()
            DrawAppTheme {
                   MainScreen(viewModel =viewModel, )
            }
        }
    }
}

