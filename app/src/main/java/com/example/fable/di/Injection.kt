package com.example.fable.di

import android.content.Context
import com.example.fable.data.StoryRepository
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.local.pref.dataStore
import com.example.fable.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(apiService,pref)
    }
}