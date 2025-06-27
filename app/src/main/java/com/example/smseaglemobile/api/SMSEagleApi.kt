package com.example.smseaglemobile.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

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

class SMSEagleApiClient(private val apiConfig: ApiConfig) {

    val api: SMSEagleApi by lazy {
        createApiInstance()
    }

    private fun createApiInstance(): SMSEagleApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()

                // Dodaj access-token jako Bearer token
                apiConfig.apiKey?.let { token ->
                    requestBuilder.addHeader("access-token", token)
                }

                // Dodaj standardowe headery
                requestBuilder.addHeader("Content-Type", "application/json")
                requestBuilder.addHeader("Accept", "application/json")

                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

//        val baseUrl = apiConfig.baseUrl ?: "https://demounit.smseagle.eu/api/v2/"

        val baseUrl = apiConfig.baseUrl ?: "https://webhook.site/09b2153d-6466-4d7e-a87a-0bdd49187bf4/"

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SMSEagleApi::class.java)
    }

    // Metoda do odświeżenia API po zmianie konfiguracji
    fun refreshApi(): SMSEagleApi {
        return createApiInstance()
    }
}
