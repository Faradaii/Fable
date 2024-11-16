package com.example.fable.view.component.myImageView

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.signature.ObjectKey
import com.example.fable.R
import com.google.android.material.imageview.ShapeableImageView as ImageView

object ImageView {
    fun ImageView.loadImage(
        context: Context,
        url: String,
        placeholder: Int? = null,
        error: Int? = null,
        signature: Key? = null,
    ) {
        Glide.with(context)
            .load(url)
            .signature(signature ?: ObjectKey(url))
            .placeholder(placeholder ?: R.drawable.ic_image_24).centerInside()
            .error(error ?: R.drawable.ic_broken_image_24).centerInside()
            .into(this)
    }
}