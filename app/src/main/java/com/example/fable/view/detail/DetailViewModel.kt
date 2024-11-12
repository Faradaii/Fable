package com.example.fable.view.detail

import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    fun load(storyId: String) =
        repository.getStory(storyId)
}