package com.naufal.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.naufal.storyapp.data.database.UserModelAuth
import com.naufal.storyapp.databinding.ActivityLoginBinding
import com.naufal.storyapp.view.main.MainActivity
import com.naufal.storyapp.view.modelFactory.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layoutView()
        setAnimation()
        loginAction()
        filterPassword()

    }

    private fun filterPassword(){
        binding.edLoginPassword.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(password: CharSequence?, after: Int, before: Int, count: Int) {
                if (password.toString().length < 8){
                    binding.edLoginPassword.error = "Password less than 8 chars"
                }else{
                    binding.edLoginPassword.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        } )
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
            val password = binding.edLoginPassword.text.toString()
            val email = binding.edLoginEmail.text.toString()
            val validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if ((password.isNotEmpty() && password.length >= 8) && (email.isNotEmpty() and validEmail)){
                viewModel.saveSession(UserModelAuth(email, "sample_token"))
                AlertDialog.Builder(this).apply {
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