package com.example.smseaglemobile.api

import com.google.gson.annotations.SerializedName

data class Status(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("id")
    val id: Int
)