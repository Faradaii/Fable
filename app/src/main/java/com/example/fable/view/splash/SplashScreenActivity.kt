package com.example.fable.view.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.databinding.ActivitySplashScreenBinding
import com.example.fable.view.HomeActivity
import com.example.fable.view.ViewModelFactory
import com.example.fable.view.profile.ProfileViewModel
import com.example.fable.view.welcome.WelcomeActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }, 2000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }, 2000)
            }
        }

        setupView()
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
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val logo = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(logo)
            startDelay = 100
        }.start()
    }
}