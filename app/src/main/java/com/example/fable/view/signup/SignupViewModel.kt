package com.example.fable.view.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fable.data.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) =
        repository.register(name, email, password)
}