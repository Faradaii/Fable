package com.example.fable.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {
    private var inputLayout: TextInputLayout? = null
    private var validationType: ValidationType? = null

    enum class ValidationType { EMAIL, PASSWORD, NAME }

    init {
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        gravity = Gravity.CENTER_VERTICAL
        isFocusable = true
        isClickable = true
        isFocusableInTouchMode = true
    }

    fun setValidationType(type: ValidationType) {
        validationType = type
        addValidation()
    }

    fun setInputLayout(layout: TextInputLayout) {
        inputLayout = layout
    }

    private fun addValidation() {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputLayout?.error = when (validationType) {
                    ValidationType.EMAIL -> validateEmail(s)
                    ValidationType.PASSWORD -> validatePassword(s)
                    ValidationType.NAME -> validateName(s)
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateEmail(s: CharSequence?): String? {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$"
        return when {
            s.isNullOrEmpty() -> "Email tidak boleh kosong"
            !s.matches(Regex(emailPattern)) -> "Email tidak valid"
            else -> null
        }
    }

    private fun validatePassword(s: CharSequence?): String? {
        return when {
            s.isNullOrEmpty() -> "Password tidak boleh kosong"
            s.length < 8 -> "Password tidak boleh kurang dari 8 karakter"
            else -> null
        }
    }

    private fun validateName(s: CharSequence?): String? {
        return when {
            s.isNullOrEmpty() -> "Name tidak boleh kosong"
            else -> null
        }
    }
}