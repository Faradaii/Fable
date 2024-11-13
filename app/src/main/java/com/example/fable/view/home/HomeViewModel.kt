package com.example.fable.view.home

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class HomeViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getAllStories() = repository.getAllStories()
}