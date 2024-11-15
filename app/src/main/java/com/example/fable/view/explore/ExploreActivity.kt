package com.example.fable.view.explore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.R

class ExploreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fcw_explore_fragment, ExploreFragment.newInstance())
                .commit()
        }

        supportActionBar?.hide()
    }
}