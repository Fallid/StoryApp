package com.naufal.storyapp.customView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

@SuppressLint("ClickableViewAccessibility")
class PasswordEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private var errorMessage = "Password less than 8 chars!"

    init {
        addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(password: CharSequence?, after: Int, before: Int, count: Int) {
                if (password.toString().length < 8){
                    setError(errorMessage, null)
                }else{
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        } )
    }

}