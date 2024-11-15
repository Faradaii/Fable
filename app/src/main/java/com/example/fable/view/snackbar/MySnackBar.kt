package com.example.fable.view.snackbar

import android.view.View
import androidx.core.content.ContextCompat
import com.example.fable.R
import com.google.android.material.snackbar.Snackbar

object MySnackBar {
    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(
                "Dismiss"
            ) {}
            .setDuration(1500)
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.md_theme_primary))
            .setTextColor(ContextCompat.getColor(view.context, R.color.md_theme_onPrimary))
            .show()
    }
}