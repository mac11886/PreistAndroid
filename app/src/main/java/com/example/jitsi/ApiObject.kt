package com.example.jitsi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiObject {

    val apiObjects = Retrofit.Builder()
        .baseUrl("").addConverterFactory(GsonConverterFactory.create()).build()
        .create(ApiService::class.java)


}