package com.example.fable.view.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fable.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateViewModel(private val repository: StoryRepository) : ViewModel() {
    var currentImageUri: Uri? = null

    fun addStory(multipartBody: MultipartBody.Part, requestBody: RequestBody) =
        repository.addStory(multipartBody, requestBody)
}