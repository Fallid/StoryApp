package com.naufal.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.naufal.storyapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        layoutView()
        setAnimation()
        filterPassword()
        signUpAction()
    }

    private fun filterPassword(){
        binding.edRegisterPassword.addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(password: CharSequence?, after: Int, before: Int, count: Int) {
                if (password.toString().length < 8){
                    binding.edRegisterPassword.error = "Password less than 8 chars"
                }else{
                    binding.edRegisterPassword.error = null
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

    private fun signUpAction() {
        val name = binding.edRegisterName.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        binding.signupButton.setOnClickListener {
            if (name.isNotEmpty() && email.isNotEmpty() && (password.isNotEmpty() && password.length >= 8)){
                AlertDialog.Builder(this).apply {
                    setTitle("Register berhasil!")
                    setMessage("Akun dengan $email berhasil dibuat. Silahkan melakukan login terlebih dahulu ya.")
                    setPositiveButton("Lanjut") { _, _ ->
                        finish()
                    }
                    create()
                    show()
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