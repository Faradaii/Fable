package com.example.fable.view.signup

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class SignupViewModel(private val repository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}