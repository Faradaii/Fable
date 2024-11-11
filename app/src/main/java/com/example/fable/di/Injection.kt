package com.example.fable.di

import android.content.Context
import com.example.fable.data.UserRepository
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.local.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}