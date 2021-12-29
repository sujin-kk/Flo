package com.example.flo

import android.annotation.SuppressLint
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {

    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView
    private lateinit var autoLoginView: AutoLoginView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun setAutoLoginView(autoLoginView: AutoLoginView) {
        this.autoLoginView = autoLoginView
    }

    fun signUp(user: User) {
//        val retrofit = Retrofit.Builder().baseUrl("http://13.125.121.202").addConverterFactory(
//            GsonConverterFactory.create()).build()
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        // API 호출 전 로딩
        signUpView.onSignUpLoading()

        // API 호출, 비동기
        authService.signUp(user).enqueue(object : Callback<AuthResponse> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("SIGNUPACT/API-RESPONSE", response.toString())

                if(response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!
                    Log.d("SIGNUPACT/API-RESPONSE-FLO", resp.toString())

                    when(resp.code) {
                        1000 -> signUpView.onSignUpSuccess()
                        else -> signUpView.onSignUpFailure(resp.code, resp.message)
                    }
                }

            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("SIGNUPACT/API-ERROR", t.message.toString())
                signUpView.onSignUpFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
    }

    fun login(user: User) {
//        val retrofit = Retrofit.Builder().baseUrl("http://13.125.121.202").addConverterFactory(
//            GsonConverterFactory.create()).build()
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        loginView.onLoginLoading()

        authService.login(user).enqueue(object: Callback<AuthResponse> {
            @SuppressLint("LongLogTag")
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("LOGINACT/API-RESPONSE", response.toString())

                if(response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!
                    Log.d("LOGINACT/API-RESPONSE-FLO", resp.toString())

                    when(resp.code) {
                        1000 -> loginView.onLoginSuccess(resp.result!!)
                        else -> loginView.onLoginFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("LOGINACT/API-ERROR", t.message.toString())
                loginView.onLoginFailure(400, "네트워크 오류가 발생했습니다.")
            }

        })
    }

    fun autoLogin(jwt: String) {
        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        autoLoginView.onAutoLoginLoading()

        authService.autoLogin(jwt).enqueue(object: Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if(response.isSuccessful && response.code() == 200) {
                    val resp = response.body()!!
                    Log.d("AUTOLOGIN/API", resp.toString())

                    when(resp.code) {
                        1000 -> autoLoginView.onAutoLoginSuccess()
                        else -> autoLoginView.onAutoLoginFailure(resp.code, resp.message)
                    }
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("AUTOLOGIN/ERROR", t.message.toString())
                autoLoginView.onAutoLoginFailure(400, "네트워크 오류가 발생했습니다.")
            }
        })
    }

}