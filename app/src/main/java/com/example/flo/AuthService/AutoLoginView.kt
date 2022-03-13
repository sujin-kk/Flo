package com.example.flo.AuthService

interface AutoLoginView {
    fun onAutoLoginLoading()
    fun onAutoLoginSuccess()
    fun onAutoLoginFailure(code: Int, message: String)
}