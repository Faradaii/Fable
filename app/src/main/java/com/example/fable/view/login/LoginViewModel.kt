package com.example.fable.view.login

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) =
        repository.login(email, password)
}