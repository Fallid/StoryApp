package com.naufal.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isLoading(false)
        layoutView()
        setAnimation()
        signUpAction()
    }

    private fun isLoading(loading: Boolean){
        if (loading){
            binding.signupProgressbar.visibility = View.VISIBLE
        }else{
            binding.signupProgressbar.visibility = View.INVISIBLE
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

    private fun signUpAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            if (name.isNotEmpty() and (email.isNotEmpty()) and password.isNotEmpty()){
                isLoading(true)
                lifecycleScope.launch {
                    try {
                        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLVBTd3VSRVdveUhtRE5fYkIiLCJpYXQiOjE3MDA4ODE0Mjl9.64IOBDHv6MQvNsKWCnldewoGS0mUgtVnbxR1rotRmYw"
                        val registerResponse = ApiConfig.getApiService(token).register(name, email, password)
                        isLoading(false)
                        if (registerResponse.error == false){
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Register berhasil!")
                                setMessage("Akun dengan $email berhasil dibuat. Silahkan melakukan login terlebih dahulu ya.")
                                setPositiveButton("Lanjut") { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                        }else{
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Register gagal!")
                                setMessage("Akun dengan $email gagal dibuat. Silahkan coba beberapa saat lagi.  \n${registerResponse.message}")
                                setPositiveButton("Tutup") { _, _ ->
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    }catch (err: Exception){
                        isLoading(false)
                        Log.e("Response error", err.toString())
                    }
                }
            }else{
                AlertDialog.Builder(this).apply {
                    setTitle("Register gagal!")
                    setMessage("Pastikan untuk mengisi data diri terlebih dahulu ya!")
                    setNegativeButton("Tutup") { _, _ -> }
                    create()
                    show()
                }
            }
        }
    }

    private fun setAnimation() {
        ObjectAnimator.ofFloat(binding.ivRegisterImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvRegisterName, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlRegisterName, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}