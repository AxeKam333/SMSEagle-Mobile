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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import com.example.smseaglemobile.api.ApiConfig
import com.example.smseaglemobile.ui.theme.SMSEagleMobileTheme

@Composable
fun ApiConfigScreen(
    apiConfig: ApiConfig? = null,
    onConfigSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val config = apiConfig ?: ApiConfig(context)

    var baseUrl by remember { mutableStateOf(config.baseUrl ?: "") }
    var apiKey by remember { mutableStateOf(config.apiKey ?: "") }

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
                config.baseUrl = baseUrl.takeIf { it.isNotEmpty() }
                config.apiKey = apiKey.takeIf { it.isNotEmpty() }
                onConfigSaved()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = baseUrl.isNotEmpty() && apiKey.isNotEmpty()
        ) {
            Text("Zapisz konfigurację")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApiConfigScreenPreview() {
    SMSEagleMobileTheme {
        ApiConfigScreen()
    }
}
