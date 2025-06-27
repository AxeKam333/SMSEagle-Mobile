package com.example.smseaglemobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface SMSEagleApi {
    @POST("messages/sms/")
    suspend fun sendSMS(
        @Header("access-token") authorization: String,
        @Body sms: SMSBody
    ): Response<List<MsgStatus>>

    @POST("messages/mms/")
    suspend fun sendMMS(@Body mms: MMSBody): Response<List<MsgStatus>>

    @POST("messages/email/")
    suspend fun sendEmail(@Body email: EmailBody): Response<List<MsgStatus>>

    @POST("calls/ring/")
    suspend fun callRing(@Body call: RingBody): Response<List<MsgStatus>>

    @POST("calls/tts/")
    suspend fun callTTS(@Body call: TTSBody): Response<List<MsgStatus>>

    @POST("calls/wave/")
    suspend fun callWave(@Body call: WaveBody): Response<List<MsgStatus>>


//    @GET("/messages")
//    suspend fun loadSMS(@Query("folder") folder: String): Response<List<>>
//
//    @GET("/messages/email")
//    suspend fun loadEmail(@Query("folder") folder: String): Response<List<>>


    companion object {
//        private const val BASE_URL = "https://demounit.smseagle.eu/api/v2/"
        private const val BASE_URL = "https://webhook.site/09b2153d-6466-4d7e-a87a-0bdd49187bf4/"

        val instance: SMSEagleApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SMSEagleApi::class.java)
        }
    }
}
