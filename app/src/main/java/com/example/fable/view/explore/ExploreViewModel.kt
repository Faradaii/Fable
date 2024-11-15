package com.example.fable.view.explore

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class ExploreViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = repository.getAllStories(location = 1)
}