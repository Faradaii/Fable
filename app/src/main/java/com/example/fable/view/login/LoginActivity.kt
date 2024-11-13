package com.example.fable.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.customView.CustomEditText
import com.example.fable.data.Result
import com.example.fable.databinding.ActivityLoginBinding
import com.example.fable.view.HomeActivity
import com.example.fable.view.MySnackBar
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            if (validateInput(email, password)) {
                viewModel.login(email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show()
                            }
                            is Result.Error -> {
                                MySnackBar.showSnackBar(
                                    binding.root,
                                    "Login Failed, Please Try Again"
                                )
                            }
                            is Result.Success -> {
                                MySnackBar.showSnackBar(
                                    binding.root,
                                    "Welcome Back, ${result.data.loginResult!!.name} !"
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }, 1000)
                            }
                        }
                    }
                }
            }
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupTextWatchers()
    }

    private fun validateInput(email: String, password: String): Boolean {
        binding.emailEditTextLayout.error = when {
            email.isEmpty() -> "Email cannot be empty"
            else -> null
        }

        binding.passwordEditTextLayout.error = when {
            password.isEmpty() -> "Password cannot be empty"
            else -> null
        }
        return binding.emailEditTextLayout.error == null && binding.passwordEditTextLayout.error == null
    }

    private fun setupTextWatchers() {

        binding.edLoginEmail.apply {
            setInputLayout(binding.emailEditTextLayout)
            setValidationType(CustomEditText.ValidationType.EMAIL)
        }

        binding.edLoginPassword.apply {
            setInputLayout(binding.passwordEditTextLayout)
            setValidationType(CustomEditText.ValidationType.PASSWORD)
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)
        val askingSignup =
            ObjectAnimator.ofFloat(binding.linearAskingSignup, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                askingSignup
            )
            startDelay = 100
        }.start()
    }

}