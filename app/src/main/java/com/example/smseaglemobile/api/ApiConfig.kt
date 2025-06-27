package com.example.smseaglemobile.api

import android.content.Context
import com.example.smseaglemobile.localdb.AppDatabase
import com.example.smseaglemobile.localdb.ApiConfigEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ApiConfig(private val context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.apiConfigDao()

    // Flow dla reaktywnego obserwowania zmian
    val configFlow: Flow<ApiConfigData?> = dao.getConfigFlow().map { entity ->
        entity?.let {
            ApiConfigData(
                baseUrl = it.baseUrl,
                apiToken = it.apiToken
            )
        }
    }

    suspend fun getConfig(): ApiConfigData? {
        return dao.getConfig()?.let {
            ApiConfigData(
                baseUrl = it.baseUrl,
                apiToken = it.apiToken
            )
        }
    }

    suspend fun saveConfig(baseUrl: String, apiToken: String) {
        val config = ApiConfigEntity(
            baseUrl = baseUrl,
            apiToken = apiToken,
            updatedAt = System.currentTimeMillis()
        )
        dao.insertConfig(config)
    }

    suspend fun clearConfig() {
        dao.clearConfig()
    }

    suspend fun hasConfig(): Boolean {
        return dao.hasConfig()
    }
}

data class ApiConfigData(
    val baseUrl: String,
    val apiToken: String
)
