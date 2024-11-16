package com.example.fable.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.fable.data.StoryRepository
import com.example.fable.data.local.entity.Story

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {
    suspend fun logout() =
        repository.logout()

    fun getAllStories(): LiveData<PagingData<Story>> =
        repository.getAllStoriesWithPager().cachedIn(viewModelScope)
}