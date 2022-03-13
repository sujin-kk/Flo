package com.example.flo.AuthService

interface LoginView {
    fun onLoginLoading()
    fun onLoginSuccess(auth: Auth)
    fun onLoginFailure(code: Int, message: String)
}