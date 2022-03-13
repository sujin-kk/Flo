package com.example.flo.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.AuthService.AuthService
import com.example.flo.AuthService.AutoLoginView
import com.example.flo.Util.MyToast
import com.example.flo.Util.getJwt
import com.example.flo.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity(), AutoLoginView {
    lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("SPLASHACT/JWT", getJwt(this))
            autoLogin()
        }, 2000) // 2초 뒤에 실행, 1초 1000 msec
    }

    private fun autoLogin() {
        val authService = AuthService()
        authService.setAutoLoginView(this)
        authService.autoLogin(getJwt(this))
    }

    override fun onAutoLoginLoading() {
        binding.splashLoadingPb.visibility = View.VISIBLE
    }

    override fun onAutoLoginSuccess() {
        binding.splashLoadingPb.visibility = View.GONE
        MyToast.createToast(this, "자동으로 로그인합니다.")?.show()
        finish()
        startNextActivity(MainActivity::class.java) // 자동 로그인 성공 시 main activiy로 이동
    }

    override fun onAutoLoginFailure(code: Int, message: String) {
        binding.splashLoadingPb.visibility = View.GONE
        when(code) {
            2000, 2021 -> {
                MyToast.createToast(this, "로그인 화면으로 이동합니다.")?.show()
                finish()
                startNextActivity(LoginActivity::class.java) // 자동 로그인 실패 시 login activity로 이동
            }
        }
    }

    private fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}