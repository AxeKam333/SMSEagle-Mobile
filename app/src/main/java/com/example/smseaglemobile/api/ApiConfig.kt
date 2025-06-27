package com.example.smseaglemobile.api

import android.content.Context
import android.content.SharedPreferences

class ApiConfig(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sms_eagle_config", Context.MODE_PRIVATE)

    var baseUrl: String?
        get() = prefs.getString("base_url", "https://demounit.smseagle.eu/api/v2/")
        set(value) = prefs.edit().putString("base_url", value).apply()

    var apiKey: String?
        get() = prefs.getString("api_key", null)
        set(value) = prefs.edit().putString("api_key", value).apply()

    fun isConfigured(): Boolean {
        return !baseUrl.isNullOrEmpty() && !apiKey.isNullOrEmpty()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
