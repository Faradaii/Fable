package com.example.fable.data.remote.retrofit

import com.example.fable.data.remote.response.GetAllResponse
import com.example.fable.data.remote.response.GetDetailResponse
import com.example.fable.data.remote.response.LoginResponse
import com.example.fable.data.remote.response.MessageResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): MessageResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,

        @Part("photo") photo: MultipartBody.Part,
        @Part("description") description: String,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): MessageResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int?
    ): GetAllResponse

    @GET("stories/{storyId}")
    suspend fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("storyId") storyId: String
    ): GetDetailResponse

}