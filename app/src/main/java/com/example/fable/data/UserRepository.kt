package com.example.fable.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.fable.data.local.pref.UserModel
import com.example.fable.data.local.pref.UserPreference
import com.example.fable.data.remote.response.LoginResponse
import com.example.fable.data.remote.response.MessageResponse
import com.example.fable.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
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
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService , userPreference)
            }.also { instance = it }
    }
}