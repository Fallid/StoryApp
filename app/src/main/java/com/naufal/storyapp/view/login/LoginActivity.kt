package com.naufal.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.naufal.storyapp.data.database.UserModelAuth
import com.naufal.storyapp.data.response.authentication.LoginResponse
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.databinding.ActivityLoginBinding
import com.naufal.storyapp.view.main.MainActivity
import com.naufal.storyapp.view.modelFactory.ViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        isLoading(false)
        setContentView(binding.root)
        layoutView()
        setAnimation()
        loginAction()

    }

    private fun isLoading (loading:Boolean){
        if (loading){
            binding.loginProgressBar.visibility = View.VISIBLE
        }else{
            binding.loginProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun layoutView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun loginAction() {
        binding.loginButton.setOnClickListener {
            isLoading(true)
            val password = binding.edLoginPassword.text.toString()
            val email = binding.edLoginEmail.text.toString()
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if ((password.isNotEmpty() && password.length >= 8) && (email.isNotEmpty() and validEmail)){
                val client = ApiConfig.getApiService().login(email, password)
                client.enqueue(object : Callback<LoginResponse>{
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        isLoading(false)
                        val loginResponseBody = response.body()
                        if (loginResponseBody != null){
                            if (loginResponseBody.error == false){
                                viewModel.saveSession(UserModelAuth(
                                    loginResponseBody.loginResult?.userId.toString(),
                                    loginResponseBody.loginResult?.name.toString(),
                                    loginResponseBody.loginResult?.token.toString(),
                                    ))
                                    AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle("Selamat Datang")
                                    setMessage("Anda berhasil login.")
                                    setPositiveButton("Lanjut") { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }else{
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle("Login gagal!")
                                    setMessage("Akun dengan $email gagal untuk login. Silahkan coba beberapa saat lagi.  \n${loginResponseBody.message}")
                                    setPositiveButton("Tutup") { _, _ ->
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Login gagal!")
                            setMessage("Akun dengan $email gagal untuk login. Silahkan coba beberapa saat lagi.  \n${t.message}")
                            setPositiveButton("Tutup") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                        Log.e("Login error", "${t.message}")
                    }

                })
            }else{
                AlertDialog.Builder(this).apply {
                    setTitle("Login Gagal")
                    setMessage("Pastikan untuk mengisi email dan password terlebih dahulu!")
                    setNegativeButton("Tutup"){ _, _ ->}
                    create()
                    show()
                }
            }
        }
    }

    private fun setAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvLoginEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlEdEmailLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvLoginPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlEdPassLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}