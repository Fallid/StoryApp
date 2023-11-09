package com.naufal.storyapp.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.naufal.storyapp.databinding.ActivityWelcomeBinding
import com.naufal.storyapp.view.login.LoginActivity
import com.naufal.storyapp.view.signup.SignupActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layoutView()
        actionButton()
        setAnimation()
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

    private fun actionButton() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setAnimation() {
        ObjectAnimator.ofFloat(binding.ivWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        ObjectAnimator.ofFloat(binding.btnLogin, View.TRANSLATION_X, 30f, -30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        ObjectAnimator.ofFloat(binding.btnSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val loginAnimator = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val signupAnimator = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)
        val titleAnimator = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val descAnimator = ObjectAnimator.ofFloat(binding.tvDescription, View.ALPHA, 1f).setDuration(100)

        val objectAnimator = AnimatorSet().apply {
            playTogether(loginAnimator, signupAnimator)
        }

        AnimatorSet().apply {
            playSequentially(titleAnimator, descAnimator, objectAnimator)
            start()
        }
    }
}