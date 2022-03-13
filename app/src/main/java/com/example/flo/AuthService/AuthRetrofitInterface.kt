package com.example.flo.AuthService

import com.example.flo.Class.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthRetrofitInterface {
    // user data
    @POST("/users")
    fun signUp(@Body user: User): Call<AuthResponse>

    // app login
    @POST("/users/login")
    fun login(@Body user: User): Call<AuthResponse>

    // auto login
    @GET ("/users/auto-login")
    fun autoLogin(@Header("X-ACCESS-TOKEN") jwt: String) : Call<AuthResponse>
}