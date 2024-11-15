package com.example.fable.di

import android.content.Context
import com.example.fable.data.StoryRepository
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.local.pref.dataStore
import com.example.fable.data.local.room.StoryDatabase
import com.example.fable.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, pref, database)
    }
}