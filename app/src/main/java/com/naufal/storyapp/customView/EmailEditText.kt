package com.naufal.storyapp.customView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

@SuppressLint("ClickableViewAccessibility")
class EmailEditText(context: Context, attrs: AttributeSet?): AppCompatEditText(context, attrs) {
    private var errorMessage = "This is not Email!"
    init {
        addTextChangedListener ( object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(email: CharSequence, after: Int, before: Int, count: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    setError(errorMessage, null)
                }else{
                    error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        } )
    }
}