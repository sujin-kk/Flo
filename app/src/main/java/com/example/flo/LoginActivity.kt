package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityLoginBinding

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity(), LoginView {

    lateinit var binding: ActivityLoginBinding
    var isHint : Boolean = false

    var idOk : Boolean = false
    var emailOk : Boolean = false
    var pwOk : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginSignUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        } // Login Activity -> SignUp Activity

        binding.loginSignInView.setOnClickListener {
            login()
            // startMainActivity()
        }

        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        binding.loginPwHintIv.setOnClickListener {
            Log.d("INPUTTYPE", binding.loginPwEt.inputType.toString())

            if(isHint) { // 비밀번호 보이는 상태이면
                isHint = false
                binding.loginPwEt.inputType = 0x00000081 // text password
                binding.loginPwHintIv.setImageResource(R.drawable.btn_input_password)
            }
            else { // 비밀번호 안보이는 상태이면
                isHint = true
                binding.loginPwEt.inputType = 0x00000091 // text visible password
                binding.loginPwHintIv.setImageResource(R.drawable.btn_input_password_off)
            }
        }

        binding.loginIdEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                idOk = binding.loginIdEt.text.toString().isNotEmpty()
                if(idOk && emailOk && pwOk) {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.mainColor))
                }
                else {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.btn_gray))
                }
            }
        })

        binding.loginEmailEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                emailOk = binding.loginEmailEt.text.toString().isNotEmpty()
                if(idOk && emailOk && pwOk) {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.mainColor))
                }
                else {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.btn_gray))
                }
            }
        })

        binding.loginPwEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                pwOk = binding.loginPwEt.text.toString().isNotEmpty()
                if(idOk && emailOk && pwOk) {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.mainColor))
                }
                else {
                    binding.loginSignInView.setBackgroundColor(resources.getColor(R.color.btn_gray))
                }
            }
        })

    }

    private fun getUser(): User {
        val email: String =  binding.loginIdEt.text.toString() + "@" + binding.loginEmailEt.text.toString()
        val pwd: String = binding.loginPwEt.text.toString()
        return User(email, pwd, "")
    }

//    private fun login() {
//        // email validation
//        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginEmailEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // pw validation
//        if(binding.loginPwEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val email: String =  binding.loginIdEt.text.toString() + "@" + binding.loginEmailEt.text.toString()
//        val pwd: String = binding.loginPwEt.text.toString()
//
//        val songDB = SongDatabase.getInstance(this)!!
//
//        val user = songDB.userDao().getUser(email, pwd)
//
//        user?.let {
//            // user가 존재하면
//            Log.d("LOGINACT/GET_USER", "userId : ${user.id}, $user")
//            // 발급받은 jwt를 저장해주는 함수
//            saveJwt(user.id)
//        }
//
//        if(user == null)
//            Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//    }

    private fun login() {
        // email validation
        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // pw validation
        if(binding.loginPwEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()
        authService.setLoginView(this)

        authService.login(getUser())
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

//    private fun saveJwt(jwt: Int) {
//        val spf = getSharedPreferences("auth", MODE_PRIVATE)
//        val editor = spf.edit()
//
//        editor.putInt("jwt", jwt)
//        editor.apply()
//    }

    override fun onLoginLoading() {
        binding.loginLoadingPb.visibility = View.VISIBLE
    }

    override fun onLoginSuccess(auth: Auth) {
        binding.loginLoadingPb.visibility = View.GONE

        saveJwt(this, auth.jwt)
        saveUserIdx(this, auth.userIdx)
        startMainActivity()
    }

    override fun onLoginFailure(code: Int, message: String) {
        binding.loginLoadingPb.visibility = View.GONE

        when(code) {
            2015, 2019, 3014 -> {
                binding.loginErrorTv.visibility = View.VISIBLE
                binding.loginErrorTv.text = message
            }
        }
    }

}