package com.example.lightimg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lightimg.core.navigation.Screen
import com.example.lightimg.domain.usecase.CompressImageUseCase
import com.example.lightimg.domain.usecase.ConvertImageUseCase
import com.example.lightimg.domain.usecase.CreatePdfUseCase
import com.example.lightimg.domain.usecase.ResizeImageUseCase
import com.example.lightimg.presentation.compress.CompressScreen
import com.example.lightimg.presentation.compress.CompressViewModel
import com.example.lightimg.presentation.components.LightImgBottomNav
import com.example.lightimg.presentation.convert.ConvertScreen
import com.example.lightimg.presentation.convert.ConvertViewModel
import com.example.lightimg.presentation.home.HomeScreen
import com.example.lightimg.presentation.pdf.PdfScreen
import com.example.lightimg.presentation.pdf.PdfViewModel
import com.example.lightimg.presentation.resize_crop.ResizeCropScreen
import com.example.lightimg.presentation.resize_crop.ResizeCropViewModel
import com.example.lightimg.presentation.settings.SettingsScreen
import com.example.lightimg.theme.Background
import com.example.lightimg.theme.LightImgTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LightImgTheme {
                LightImgApp(context = this)
            }
        }
    }
}

@Composable
fun LightImgApp(context: android.content.Context) {
    var currentScreen: Screen by remember { mutableStateOf(Screen.Home) }
    var showSettings by remember { mutableStateOf(false) }

    // Manual DI — use cases created with application context
    val compressUseCase = remember { CompressImageUseCase(context) }
    val resizeUseCase   = remember { ResizeImageUseCase(context) }
    val convertUseCase  = remember { ConvertImageUseCase(context) }
    val pdfUseCase      = remember { CreatePdfUseCase(context) }

    if (showSettings) {
        SettingsScreen(onBack = { showSettings = false })
        return
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            LightImgBottomNav(
                currentScreen = currentScreen,
                onNavigate    = { currentScreen = it },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues),
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    onNavigate     = { currentScreen = it },
                    onSettingsClick = { showSettings = true },
                )

                Screen.Compress -> {
                    val vm = remember { CompressViewModel(compressUseCase) }
                    CompressScreen(viewModel = vm)
                }

                Screen.ResizeCrop -> {
                    val vm = remember { ResizeCropViewModel(resizeUseCase) }
                    ResizeCropScreen(viewModel = vm)
                }

                Screen.Convert -> {
                    val vm = remember { ConvertViewModel(convertUseCase) }
                    ConvertScreen(viewModel = vm)
                }

                Screen.Pdf -> {
                    val vm = remember { PdfViewModel(pdfUseCase) }
                    PdfScreen(viewModel = vm)
                }

                Screen.Settings -> SettingsScreen(onBack = { currentScreen = Screen.Home })
            }
        }
    }
}
