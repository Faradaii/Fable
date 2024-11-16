package com.example.fable.view.signup

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
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.R
import com.example.fable.customView.CustomEditText
import com.example.fable.data.Result
import com.example.fable.databinding.ActivitySignupBinding
import com.example.fable.util.Util
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.component.snackbar.MySnackBar
import com.example.fable.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelFactory.getInstance(this).create(SignupViewModel::class.java)

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

            if (validateCheckbox()) {
                viewModel.register(name, email, password).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                Toast.makeText(
                                    this,
                                    getString(R.string.adding_you_to_our_world),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Error -> {
                                MySnackBar.showSnackBar(
                                    binding.root,
                                    result.error
                                )
                            }
                            is Result.Success -> {
                                MySnackBar.showSnackBar(
                                    binding.root,
                                    getString(R.string.register_successfully_please_login)
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }, Util.ONE_SECOND)
                            }
                        }
                    }
                }
            }
        }
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        setupTextWatchers()
    }

    private fun validateCheckbox(): Boolean {

        binding.checkboxEditTextLayout.error = when {
            !binding.checkBox.isChecked -> getString(R.string.you_must_agree_to_the_terms_to_proceed)
            else -> null
        }

        return binding.checkboxEditTextLayout.error == null
    }

    private fun setupTextWatchers() {

        binding.edRegisterName.apply {
            setInputLayout(binding.nameEditTextLayout)
            setValidationType(CustomEditText.ValidationType.NAME)
        }

        binding.edRegisterEmail.apply {
            setInputLayout(binding.emailEditTextLayout)
            setValidationType(CustomEditText.ValidationType.EMAIL)
        }

        binding.edRegisterPassword.apply {
            setInputLayout(binding.passwordEditTextLayout)
            setValidationType(CustomEditText.ValidationType.PASSWORD)
        }

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
        val askingLogin =
            ObjectAnimator.ofFloat(binding.linearAskingLogin, View.ALPHA, 1f).setDuration(100)


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
                signup,
                askingLogin
            )
            startDelay = 100
        }.start()
    }
}