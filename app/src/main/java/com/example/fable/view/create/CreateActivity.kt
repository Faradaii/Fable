package com.example.fable.view.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fable.R

class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fcv_create_fragment, CreateFragment.newInstance())
                .commit()
        }
    }
}