package com.example.smseaglemobile.api

data class SMSBody(
    val to: List<String>? = null,
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val text: String? = null,
    val encoding: String? = null,
    val test: Boolean? = null
)

data class MMSContent (
    val content_type: String,
    val content: String
)

data class MMSBody(
    val to: List<String>? = null,
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val text: String? = null,
    val attachments: List<MMSContent>? = null,
    val encoding: String? = null
)

data class EmailBody(
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val subject: String? = null,
    val text: String? = null,
    val attachments: List<MMSContent>? = null
)

data class RingBody(
    val to: List<String>? = null,
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val duration: Int? = null
)

data class TTSBody(
    val to: List<String>? = null,
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val text: String? = null,
    val duration: Int? = null
)

//data class TTSAdvancedBody(
//    val to: List<String>? = null,
//    val contacts: List<Int>? = null,
//    val groups: List<Int>? = null,
//    val text: String? = null,
//    val voice_id: Int? = null,
//    val duration: Int? = null
//)

data class WaveBody(
    val to: List<String>? = null,
    val contacts: List<Int>? = null,
    val groups: List<Int>? = null,
    val wave_id: Int? = null,
    val content: String? = null,
    val duration: Int? = null
)