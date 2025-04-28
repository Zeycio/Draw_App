package com.zeycio.drawapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.zeycio.drawapp.ui.theme.DrawAppTheme
import com.zeycio.drawapp.ui.theme.scree.draw.view.MainScreen
import com.zeycio.drawapp.ui.theme.scree.draw.viewmodel.ImageTransformViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = ImageTransformViewModel()
            DrawAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   MainScreen(viewModel =viewModel, modifier = Modifier.padding(innerPadding) )
                }
            }
        }
    }
}

