package com.naufal.storyapp.customView

import android.annotation.SuppressLint
import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import com.naufal.storyapp.R

@SuppressLint("ClickableViewAccessibility")
class PasswordEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private var isPasswordVisible = false

    init {
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_lock_24, 0)
        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                    togglePasswordVisibility()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        transformationMethod = if (isPasswordVisible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
    }
}