package com.example.fable.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.fable.data.local.pref.UserModel
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.remote.response.GetAllResponse
import com.example.fable.data.remote.response.GetDetailResponse
import com.example.fable.data.remote.response.LoginResponse
import com.example.fable.data.remote.response.MessageResponse
import com.example.fable.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    fun register(name: String, email: String, password: String): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(Result.Success(successResponse))

        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "An error occurred"))

        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val message = apiService.login(email, password)
            emit(Result.Success(message))
            val userLogged = UserModel(
                message.loginResult!!.userId.toString(),
                email,
                message.loginResult.name.toString(),
                message.loginResult.token.toString(),
                true
            )
            saveSession(userLogged)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, MessageResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage ?: "An error occurred"))
        }
    }

    fun getAllStories(): LiveData<Result<GetAllResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAllStories()
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "An error occurred"))
        }
    }

    fun addStory(multipartBody: MultipartBody.Part, requestBody: RequestBody): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addStory(multipartBody, requestBody)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "An error occurred"))
        }
    }

    private suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStory(storyId: String): LiveData<Result<GetDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(storyId)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "An error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService , userPreference)
            }.also { instance = it }
    }
}