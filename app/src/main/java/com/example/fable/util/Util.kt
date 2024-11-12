package com.example.fable.util
import java.text.SimpleDateFormat
import java.util.Locale

object Util {

    fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id"))

        return try {
            val formatedDate = inputFormat.parse(date)
            outputFormat.format(formatedDate ?: "")
        } catch (e: Exception) {
            date
        }
    }
}