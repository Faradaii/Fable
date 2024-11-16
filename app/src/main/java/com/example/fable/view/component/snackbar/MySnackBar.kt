package com.example.fable.view.component.snackbar

import android.view.View
import androidx.core.content.ContextCompat
import com.example.fable.R
import com.example.fable.util.Util
import com.google.android.material.snackbar.Snackbar

object MySnackBar {
    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(
                "Close"
            ) {}
            .setDuration(Util.TWO_SECONDS.toInt())
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setBackgroundTint(ContextCompat.getColor(view.context, R.color.md_theme_primary))
            .setTextColor(ContextCompat.getColor(view.context, R.color.md_theme_onPrimary))
            .show()
    }
}