package com.example.fable.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.fable.data.StoryRepository
import com.example.fable.data.local.pref.UserModel

class ProfileViewModel(private val repository: StoryRepository) : ViewModel() {
    suspend fun logout() =
        repository.logout()


    fun getUser(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}