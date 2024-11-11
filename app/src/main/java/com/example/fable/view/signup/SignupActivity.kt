package com.example.fable.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
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

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (validateInput(name, email, password)) {
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                    setPositiveButton("Lanjut") { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            }
        }

        setupTextWatchers()
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$"
        val isEmailValid = email.matches(Regex(emailPattern))

        binding.nameEditTextLayout.error = when {
            name.isEmpty() -> "Name tidak boleh kosong"
            else -> null
        }

        binding.emailEditTextLayout.error = when {
            email.isEmpty() -> "Email tidak boleh kosong"
            !isEmailValid -> "Email tidak valid"
            else -> null
        }

        binding.passwordEditTextLayout.error = when {
            password.isEmpty() -> "Password tidak boleh kosong"
            password.length < 8 -> "Password tidak boleh kurang dari 8 karakter"
            else -> null
        }

        binding.checkboxEditTextLayout.error = when {
            !binding.checkBox.isChecked -> "You must agree to the terms to proceed."
            else -> null
        }

        return binding.nameEditTextLayout.error == null && binding.emailEditTextLayout.error == null && binding.passwordEditTextLayout.error == null && binding.checkboxEditTextLayout.error == null
    }

    private fun setupTextWatchers() {

        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.nameEditTextLayout.error = when {
                    s.isNullOrEmpty() -> "Name tidak boleh kosong"
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$"
                val isEmailValid = s.toString().matches(Regex(emailPattern))

                binding.emailEditTextLayout.error = when {
                    s.isNullOrEmpty() -> "Email tidak boleh kosong"
                    !isEmailValid -> "Email tidak valid"
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.passwordEditTextLayout.error = when {
                    s.isNullOrEmpty() -> "Password tidak boleh kosong"
                    s.length < 8 -> "Password tidak boleh kurang dari 8 karakter"
                    else -> null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxEditTextLayout.error = null
            }
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView2, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val checkboxEditTextLayout =
            ObjectAnimator.ofFloat(binding.checkboxEditTextLayout, View.ALPHA, 1f).setDuration(100)
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
                checkboxEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}