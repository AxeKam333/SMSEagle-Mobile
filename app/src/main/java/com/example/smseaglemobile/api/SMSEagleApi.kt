package com.example.smseaglemobile.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking

interface SMSEagleApi {
    @POST("messages/sms")
    suspend fun sendSMS(@Body sms: SMSBody): Response<List<MsgStatus>>

    @POST("messages/mms")
    suspend fun sendMMS(@Body mms: MMSBody): Response<List<MsgStatus>>

    @POST("messages/email")
    suspend fun sendEmail(@Body email: EmailBody): Response<List<MsgStatus>>

    @POST("calls/ring")
    suspend fun callRing(@Body call: RingBody): Response<List<MsgStatus>>

    @POST("calls/tts")
    suspend fun callTTS(@Body call: TTSBody): Response<List<MsgStatus>>

    @POST("calls/wave")
    suspend fun callWave(@Body call: WaveBody): Response<List<MsgStatus>>
}

class SMSEagleApiClient {
    private var baseUrl: String = ""
    private var apiToken: String = ""

    constructor(apiConfig: ApiConfig) {
        runBlocking {
            val config = apiConfig.getConfig()
            if (config != null) {
                baseUrl = config.baseUrl
                apiToken = config.apiToken
            }
        }
    }

    constructor(baseUrl: String, apiToken: String) {
        this.baseUrl = baseUrl
        this.apiToken = apiToken
    }

    fun api(): SMSEagleApi? {
        if (baseUrl.isEmpty() || apiToken.isEmpty()) return null
        return createApiInstance(baseUrl, apiToken)
    }

    private fun createApiInstance(baseUrl: String, apiToken: String): SMSEagleApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SMSEagleApi::class.java)
    }
}
