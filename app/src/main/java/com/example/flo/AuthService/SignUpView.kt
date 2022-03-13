package com.example.flo.AuthService

interface SignUpView {
    fun onSignUpLoading()
    fun onSignUpSuccess()
    fun onSignUpFailure(code: Int, message: String)
}