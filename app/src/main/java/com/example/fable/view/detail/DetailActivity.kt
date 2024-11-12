package com.example.fable.view.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fable.R
import com.example.fable.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var activityDetailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(activityDetailBinding.root)

        val storyId = intent.getStringExtra(EXTRA_ID) ?: ""

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainerView, DetailFragment.newInstance(storyId))
                .commit()
        }
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}