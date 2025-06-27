package com.example.smseaglemobile.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.smseaglemobile.api.ApiConfig
import com.example.smseaglemobile.ui.theme.SMSEagleMobileTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ApiConfigScreen(
    apiConfig: ApiConfig,
    onConfigSaved: () -> Unit
) {
    var baseUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Załaduj istniejącą konfigurację
    LaunchedEffect (Unit) {
        apiConfig.getConfig()?.let { config ->
            baseUrl = config.baseUrl
            apiKey = config.apiToken
        }
    }

    fun String.saveUrl(): String {
        var url = this.trim()
        // Dodaj https:// jeśli nie ma żadnego prefiksu
        url = when {
            url.startsWith("https://") -> url
            url.startsWith("http://") -> url
            else -> "https://$url"
        }
        // Dodaj / na końcu, jeśli go brakuje
        if (!url.endsWith("/")) url += "/"
        return url
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Konfiguracja API",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL") },
            placeholder = { Text("https://your-smseagle.com/api/v2/") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("Access Token") },
            placeholder = { Text("Wprowadź swój access token") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                isLoading = true
                // Użyj coroutine scope
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        apiConfig.saveConfig(baseUrl.saveUrl(), apiKey)
                        withContext(Dispatchers.Main) {
                            onConfigSaved()
                        }
                    } catch (e: Exception) {
                        // Obsłuż błąd
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && baseUrl.isNotEmpty() && apiKey.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Zapisz konfigurację")
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ApiConfigScreenPreview() {
//    SMSEagleMobileTheme {
//        ApiConfigScreen()
//    }
//}
