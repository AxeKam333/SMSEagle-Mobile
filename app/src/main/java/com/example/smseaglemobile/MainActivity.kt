package com.example.smseaglemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smseaglemobile.api.ApiConfig
import com.example.smseaglemobile.api.SMSEagleApiClient
import com.example.smseaglemobile.ui.components.ApiConfigScreen
import com.example.smseaglemobile.ui.components.SMSScreen
import com.example.smseaglemobile.ui.theme.SMSEagleMobileTheme
import com.example.smseaglemobile.viewmodel.SMSViewModel

import androidx.compose.runtime.compositionLocalOf

val ecoMode = compositionLocalOf { EcoModeState() }

class EcoModeState {
    var isEco by mutableStateOf(false)
    fun toggle() { isEco = !isEco }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val ecoModeState = remember { EcoModeState() }
            CompositionLocalProvider (ecoMode provides ecoModeState) {
                SMSEagleMobileTheme(
                    darkTheme = ecoModeState.isEco
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainContent()
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent() {
    val context = LocalContext.current
    val apiConfig = remember { ApiConfig(context) }
    val apiClient = remember { SMSEagleApiClient(apiConfig) }
    var showConfig by remember { mutableStateOf<Boolean?>(null) }
    val navController = rememberNavController()

    // Ładuj stan konfiguracyjny asynchronicznie
    LaunchedEffect (Unit) {
        showConfig = !(apiConfig.hasConfig())
    }

    NavHost(
        navController = navController,
        startDestination = "loading_screen"
    ) {
        composable("loading_screen") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        composable("sms_screen") {
            val smsViewModel: SMSViewModel = viewModel {
                SMSViewModel(apiClient)
            }
            SMSScreen(
                viewModel = smsViewModel,
                onConfig = {
                    navController.navigate("config_screen")
                }
            )
        }

        composable("config_screen") {
            val smsViewModel: SMSViewModel = viewModel {
                SMSViewModel(apiClient)
            }
            ApiConfigScreen(
                apiConfig = apiConfig,
                onConfigSaved = {
                    showConfig = false
                    navController.navigate("sms_screen")
                }
            )
        }
    }

    // Automatyczne przekierowanie po załadowaniu konfiguracji
    LaunchedEffect(apiConfig) {
        if (apiConfig.hasConfig()) {
            navController.navigate("sms_screen") {
                popUpTo("loading_screen") { inclusive = true }
            }
        } else {
            navController.navigate("config_screen") {
                popUpTo("loading_screen") { inclusive = true }
            }
        }
    }
}
