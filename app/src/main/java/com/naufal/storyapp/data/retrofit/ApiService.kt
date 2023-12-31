package com.naufal.storyapp.data.retrofit

import com.naufal.storyapp.data.response.authentication.LoginResponse
import com.naufal.storyapp.data.response.authentication.RegisterResponse
import com.naufal.storyapp.data.response.story.AddStoryResponse
import com.naufal.storyapp.data.response.story.AllStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): AllStoryResponse


    @Multipart
    @POST("stories")
    fun newStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): Call<AddStoryResponse>

    @Multipart
    @POST("stories")
    fun newStoryWithoutLocation(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        ): Call<AddStoryResponse>

    @GET("stories?location=1")
    suspend fun getLocation(
        @Query("location") location: Int = 1
    ): AllStoryResponse
}