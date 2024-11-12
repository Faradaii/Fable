package com.example.fable.view.home

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class HomeViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getAllStories() = storyRepository.getAllStories()
}