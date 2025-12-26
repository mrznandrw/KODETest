package com.mrzn.kodetest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mrzn.kodetest.presentation.main.MainScreen
import com.mrzn.kodetest.presentation.ui.theme.KODETestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KODETestTheme {
                MainScreen()
            }
        }
    }
}
