package com.example.flo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://13.125.121.202"

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://13.125.121.202")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit
}