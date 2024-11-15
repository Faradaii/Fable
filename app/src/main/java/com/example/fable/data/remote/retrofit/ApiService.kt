package com.example.fable.data.remote.retrofit

import com.example.fable.data.remote.response.GetAllResponse
import com.example.fable.data.remote.response.GetDetailResponse
import com.example.fable.data.remote.response.LoginResponse
import com.example.fable.data.remote.response.MessageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null
    ): MessageResponse

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): GetAllResponse

    @GET("stories/{storyId}")
    suspend fun getDetailStory(
        @Path("storyId") storyId: String
    ): GetDetailResponse

}