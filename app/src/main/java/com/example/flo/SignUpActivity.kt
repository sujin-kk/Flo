package com.example.flo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity(), SignUpView {

    lateinit var binding: ActivitySignUpBinding

    var isPwHint : Boolean = false
    var isPwCheckHint : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpDoneBtn.setOnClickListener {
            signUp()
        }

        binding.signUpBackIv.setOnClickListener {
            finish()
        }

        binding.signUpPwHintIv.setOnClickListener {

            if(isPwHint) { // 비밀번호 보이는 상태이면
                isPwHint = false
                binding.signUpPwEt.inputType = 0x00000081 // text password
                binding.signUpPwHintIv.setImageResource(R.drawable.btn_input_password)
            }
            else { // 비밀번호 안보이는 상태이면
                isPwHint = true
                binding.signUpPwEt.inputType = 0x00000091 // text visible password
                binding.signUpPwHintIv.setImageResource(R.drawable.btn_input_password_off)
            }
        }

        binding.signUpPwCheckHintIv.setOnClickListener {

            if(isPwCheckHint) { // 비밀번호 보이는 상태이면
                isPwCheckHint = false
                binding.signUpPwCheckEt.inputType = 0x00000081 // text password
                binding.signUpPwCheckHintIv.setImageResource(R.drawable.btn_input_password_off)
            }
            else { // 비밀번호 안보이는 상태이면
                isPwCheckHint = true
                binding.signUpPwCheckEt.inputType = 0x00000091 // text visible password
                binding.signUpPwCheckHintIv.setImageResource(R.drawable.btn_input_password)
            }
        }
    }

    private fun getUser(): User {
        val email: String =  binding.signUpIdEt.text.toString() + "@" + binding.signUpEmailEt.text.toString()
        val pwd: String = binding.signUpPwEt.text.toString()
        val name: String = binding.signUpNameEt.text.toString()
        return User(email, pwd, name)
    }

//    private fun signUp() {
//        // email validation
//        if(binding.signUpIdEt.text.toString().isEmpty() || binding.signUpEmailEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // pw validation
//        if(binding.signUpPwEt.text.toString().isEmpty() != binding.signUpPwCheckEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val userDB = SongDatabase.getInstance(this)!!
//        userDB.userDao().insert(getUser())
//
//        val users = userDB.userDao().getUsers()
//        Log.d("SIGNUPACT", users.toString())
//    }

    private fun signUp() {
        // email validation
        if(binding.signUpIdEt.text.toString().isEmpty() || binding.signUpEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // name validation
        if(binding.signUpNameEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이름 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // pw validation
        if(binding.signUpPwEt.text.toString().isEmpty() != binding.signUpPwCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()
        authService.setSignUpView(this)
        authService.signUp(getUser())

        Log.d("SIGNUPACT/ASYNC", "Hello, ")
    }

    override fun onSignUpLoading() {
         binding.signUpLoadingPb.visibility = View.VISIBLE
    }

    override fun onSignUpSuccess() {
        binding.signUpLoadingPb.visibility = View.GONE

        finish()
    }

    override fun onSignUpFailure(code: Int, message: String) {
        binding.signUpLoadingPb.visibility = View.GONE

        when(code) {
            2016, 2017 -> {
                binding.signUpEmailErrorTv.visibility = View.VISIBLE
                binding.signUpEmailErrorTv.text = message
            }
        }
    }

}