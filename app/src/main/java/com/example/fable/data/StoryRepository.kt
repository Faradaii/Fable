package com.example.fable.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.fable.data.local.entity.Story
import com.example.fable.data.local.pref.UserModel
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.local.room.StoryDatabase
import com.example.fable.data.remote.response.GetAllResponse
import com.example.fable.data.remote.response.GetDetailResponse
import com.example.fable.data.remote.response.LoginResponse
import com.example.fable.data.remote.response.MessageResponse
import com.example.fable.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase,
) {

    fun register(name: String, email: String, password: String): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val successResponse = apiService.register(name, email, password)
            emit(Result.Success(successResponse))

        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message!!))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request timed out. Please try again."))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val message = apiService.login(email, password)
            val userLogged = UserModel(
                message.loginResult!!.userId.toString(),
                email,
                message.loginResult.name.toString(),
                message.loginResult.token.toString(),
                true
            )
            saveSession(userLogged)
            emit(Result.Success(message))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message!!))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request timed out. Please try again."))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred"))
        }
    }

    fun getAllStories(
        page: Int? = null,
        size: Int? = null,
        location: Int? = null,
    ): LiveData<Result<GetAllResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.getAllStories(page, size, location)
            }
            delay(2000)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message!!))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request timed out. Please try again."))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred"))
        }
    }

    fun getAllStoriesWithPager(): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun addStory(
        multipartBody: MultipartBody.Part,
        descRequestBody: RequestBody,
        latitudeRequestBody: RequestBody? = null,
        longitudeRequestBody: RequestBody? = null,
    ): LiveData<Result<MessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addStory(
                multipartBody,
                descRequestBody,
                latitudeRequestBody,
                longitudeRequestBody
            )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message!!))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request timed out. Please try again."))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred"))
        }
    }

    fun getStory(storyId: String): LiveData<Result<GetDetailResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getDetailStory(storyId)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(jsonInString, MessageResponse::class.java)
            emit(Result.Error(errorResponse.message!!))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error("Request timed out. Please try again."))
        } catch (e: Exception) {
            emit(Result.Error("An unexpected error occurred"))
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


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase,
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(
                    apiService = apiService,
                    userPreference = userPreference,
                    storyDatabase = storyDatabase
                )
            }.also { instance = it }
    }
}