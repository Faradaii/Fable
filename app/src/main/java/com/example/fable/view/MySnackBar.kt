package com.example.fable.view

import android.view.View
import com.google.android.material.snackbar.Snackbar

object MySnackBar {
    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}