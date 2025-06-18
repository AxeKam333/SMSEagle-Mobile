package com.example.smseaglemobile.api

import com.example.smseaglemobile.api.CocktailResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CocktailApi {
    @GET("search.php")
    suspend fun searchCocktails(@Query("s") searchQuery: String): CocktailResponse

    @GET("lookup.php")
    suspend fun getCocktailDetails(@Query("i") id: String): CocktailResponse

    @GET("filter.php")
    suspend fun getByAlcoholic(@Query("a") alcoholic: String): CocktailResponse

    @GET("filter.php")
    suspend fun getByCategory(@Query("c") category: String): CocktailResponse

    companion object {
        private const val BASE_URL = "https://www.example.com/"

        val instance: CocktailApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CocktailApi::class.java)
        }
    }
}

interface SMSEagleApi {
    @POST("/messages/sms")
    suspend fun sendSMS(): Response<Status>
    /*suspend fun sendSMS(@Body sms: SMSBody): Response<Status>
    */
    
    @POST("/messages/mms")
    suspend fun sendMMS(): Response<Status>

    @POST("/messages/email")
    suspend fun sendEmail(): Response<Status>

    @POST("/calls/ring")
    suspend fun callRing(): Response<Status>

    @POST("/calls/tts")
    suspend fun callTTS(): Response<Status>

    @POST("/calls/wave")
    suspend fun callWave(): Response<Status>

    @GET("/messages")
    suspend fun loadSMSMessages(@Query("folder") folder: String): messageInfo

    @GET("/messages/email")
    suspend fun loadSMSMessages(@Query("folder") folder: String): emailInfo

    
    companion object {
        private const val BASE_URL = "https://www.example.com/"

        val instance: SMSEagleApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CocktailApi::class.java)
        }
    }
}
