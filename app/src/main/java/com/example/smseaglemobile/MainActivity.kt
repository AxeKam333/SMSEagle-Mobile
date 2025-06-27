package com.example.smseaglemobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smseaglemobile.api.ApiConfig
import com.example.smseaglemobile.api.SMSEagleApiClient
import com.example.smseaglemobile.ui.components.ApiConfigScreen
import com.example.smseaglemobile.ui.components.SMSScreen
import com.example.smseaglemobile.ui.theme.SMSEagleMobileTheme
import com.example.smseaglemobile.viewmodel.SMSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SMSEagleMobileTheme {
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

@Composable
fun MainContent() {
    val context = LocalContext.current
    val apiConfig = remember { ApiConfig(context) }
    val apiClient = remember { SMSEagleApiClient(apiConfig) }
    var showConfig by remember { mutableStateOf(!apiConfig.isConfigured()) }

    if (showConfig) {
        ApiConfigScreen(
            apiConfig = apiConfig,
            onConfigSaved = {
                showConfig = false
            }
        )
    } else {
        val smsViewModel: SMSViewModel = viewModel {
            SMSViewModel(apiClient)
        }
        SMSScreen(viewModel = smsViewModel)
    }
}