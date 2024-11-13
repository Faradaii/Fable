package com.example.fable.view.alert

import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper
import com.example.fable.R

object CustomAlertDialog {
    private var dialog: AlertDialog? = null

    fun showDialog(context: Context, title: String, message: String?, type: Type) {
        closeDialog()
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
        builder.apply {
            setTitle(title)
            setMessage(message)
            when (type) {
                Type.SUCCESS -> setPositiveButton("Okay") { dialog, _ -> dialog?.dismiss() }
                Type.ERROR -> setPositiveButton("Try Again") { dialog, _ -> dialog?.dismiss() }
                Type.LOADING -> setView(R.layout.loading_info)
            }
        }

        dialog = builder.create()
        dialog?.show()
    }

    fun closeDialog() {
        dialog?.dismiss()
        dialog = null
    }

    enum class Type {
        SUCCESS,
        ERROR,
        LOADING
    }
}
