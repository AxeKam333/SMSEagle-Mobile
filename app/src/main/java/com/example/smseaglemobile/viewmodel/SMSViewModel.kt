package com.example.smseaglemobile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smseaglemobile.api.SMSEagleApiClient
import com.example.smseaglemobile.api.SMSBody
import com.example.smseaglemobile.api.MsgStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SMSViewModel(
    private val apiClient: SMSEagleApiClient
) : ViewModel() {

    // Stan dla wyników wysyłania SMS-ów
    private val _smsResults = MutableStateFlow<List<MsgStatus>>(emptyList())
    val smsResults: StateFlow<List<MsgStatus>> = _smsResults

    // Stan ładowania
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Stan błędu
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // Stan sukcesu
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

    private val api = apiClient.api

    fun sendSMS(
        to: List<String>? = null,
        contacts: List<Int>? = null,
        groups: List<Int>? = null,
        text: String,
        encoding: String? = null,
        test: Boolean? = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _isSuccess.value = false

            try {
                val smsBody = SMSBody(
                    to = to,
                    contacts = contacts,
                    groups = groups,
                    text = text,
                    encoding = encoding,
                    test = test
                )

                val response = api.sendSMS(smsBody)

                if (response.isSuccessful) {
                    response.body()?.let { results ->
                        _smsResults.value = results
                        _isSuccess.value = true
                        Log.d("SMSViewModel", "SMS sent successfully: ${results.size} messages")
                    } ?: run {
                        _errorMessage.value = "Pusta odpowiedź z serwera"
                    }
                } else {
                    _errorMessage.value = "Błąd HTTP: ${response.code()} - ${response.message()}"
                    Log.e("SMSViewModel", "HTTP Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Błąd podczas wysyłania SMS: ${e.message}"
                Log.e("SMSViewModel", "Error sending SMS", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Wysyła SMS do konkretnych numerów telefonów
     */
    fun sendSMSToNumbers(numbers: List<String>, text: String, test: Boolean = false) {
        sendSMS(to = numbers, text = text, test = test)
    }

    /**
     * Wysyła SMS do kontaktów z bazy danych (po ID)
     */
    fun sendSMSToContacts(contactIds: List<Int>, text: String, test: Boolean = false) {
        sendSMS(contacts = contactIds, text = text, test = test)
    }

    /**
     * Wysyła SMS do grup kontaktów (po ID grup)
     */
    fun sendSMSToGroups(groupIds: List<Int>, text: String, test: Boolean = false) {
        sendSMS(groups = groupIds, text = text, test = test)
    }

    /**
     * Czyści komunikat o błędzie
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Czyści stan sukcesu
     */
    fun clearSuccess() {
        _isSuccess.value = false
    }

    /**
     * Czyści wyniki SMS-ów
     */
    fun clearResults() {
        _smsResults.value = emptyList()
    }

    /**
     * Sprawdza czy wszystkie SMS-y zostały wysłane pomyślnie
     */
    fun areAllMessagesSuccessful(): Boolean {
        return _smsResults.value.all { it.status.lowercase() == "ok" || it.status.lowercase() == "success" }
    }

    /**
     * Zwraca liczbę pomyślnie wysłanych wiadomości
     */
    fun getSuccessfulMessagesCount(): Int {
        return _smsResults.value.count { it.status.lowercase() == "ok" || it.status.lowercase() == "success" }
    }

    /**
     * Zwraca liczbę nieudanych wiadomości
     */
    fun getFailedMessagesCount(): Int {
        return _smsResults.value.count { it.status.lowercase() != "ok" && it.status.lowercase() != "success" }
    }
}
