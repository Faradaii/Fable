package com.example.fable.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.fable.data.StoryRepository
import com.example.fable.data.local.pref.UserModel

class SplashScreenViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}