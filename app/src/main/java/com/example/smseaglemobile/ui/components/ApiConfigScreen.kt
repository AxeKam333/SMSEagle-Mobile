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
import com.example.smseaglemobile.api.ApiConfig
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.collectAsState
import com.example.smseaglemobile.api.MsgStatus
import com.example.smseaglemobile.api.SMSEagleApiClient
import com.example.smseaglemobile.viewmodel.SMSViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ApiConfigScreen(
    apiConfig: ApiConfig,
    onConfigSaved: () -> Unit,
    viewModel: SMSViewModel
) {

    var baseUrl by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    var testResult by remember { mutableStateOf<MsgStatus?>(null) }
    var testError by remember { mutableStateOf<String?>(null) }
    var testLoading by remember { mutableStateOf(false) }

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

        url = when {
            url.endsWith("api/v2/") -> url
            else -> url+"api/v2/"
        }

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
                testLoading = true
                testError = null
                testResult = null

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // 2. Stwórz NOWEGO klienta API z nową konfiguracją
                        val testApiClient = SMSEagleApiClient(
                            baseUrl = baseUrl.saveUrl(),
                            apiToken = apiKey
                        )

                        // 3. Stwórz NOWY ViewModel z nowym klientem
                        val testViewModel = SMSViewModel(testApiClient)

                        // 4. Wyślij wiadomość testową
                        testViewModel.sendSMSToNumbers(listOf("1"), "Test", true)

                        // 5. Poczekaj na odpowiedź
                        testViewModel.isLoading
                            .filter { it } // Czekamy aż isLoading stanie się false
                            .first() // Oczekujemy pierwszej wartości false
                        println("skonczylem "+testViewModel.isLoading.value)

                        testViewModel.isLoading
                            .filter { !it } // Czekamy aż isLoading stanie się false
                            .first() // Oczekujemy pierwszej wartości false
                        println("skonczylem "+testViewModel.isLoading.value)

                        // 6. Zapisz wynik testu
                        testError = testViewModel.errorMessage.value
                        testResult = testViewModel.smsResults.value.firstOrNull()
                        println(testViewModel.errorMessage.value)
                        println(testViewModel.smsResults.value.firstOrNull())

                    } catch (e: Exception) {
                        testError = e.message ?: "Nieznany błąd"
                    } finally {
                        testLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !testLoading && baseUrl.isNotEmpty() && apiKey.isNotEmpty()
        ) {
            if (testLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Testuj konfigurację")
        }

        // Przycisk zapisujący konfigurację
        Button(
            onClick = {
                loading = true
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Zapisz tylko jeśli test był udany
                        if (testResult != null) {
                            apiConfig.saveConfig(baseUrl.saveUrl(), apiKey)
                            withContext(Dispatchers.Main) {
                                onConfigSaved()
                            }
                        }
                    } catch (e: Exception) {
                        // Obsłuż błąd
                    } finally {
                        loading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && baseUrl.isNotEmpty() && apiKey.isNotEmpty() && testResult != null
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Zapisz konfigurację")
        }

        // Wyświetl wyniki testu
        testResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Test udany: ${result.status}",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        testError?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Błąd testu: $error",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
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
