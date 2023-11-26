package com.naufal.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.database.UserModelAuth
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.data.retrofit.ApiService
import com.naufal.storyapp.view.paging.StoryPaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserAuthRepository private constructor(
    private val userPreference: UserAuthPreference,
    private val apiService: ApiService
) {
    suspend fun saveSession(user: UserModelAuth) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModelAuth> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }


    fun addStory(
        description: String,
        imageFile: File,
        latitude: Double,
        longitude: Double
    ) = liveData {
        emit(ResultProcess.Loading)
        val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
        val latitudeRequestBody = latitude.toString().toRequestBody(MultipartBody.FORM)
        val longitudeRequestBody = longitude.toString().toRequestBody(MultipartBody.FORM)
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val successResponse =
                apiService.newStory(descriptionRequestBody,multipartBody, latitudeRequestBody, longitudeRequestBody)
            emit(ResultProcess.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            emit(ResultProcess.Error(errorBody.toString()))
        }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPaging(apiService)
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserAuthRepository? = null
        fun getInstance(
            userPreference: UserAuthPreference,
            apiService: ApiService
        ): UserAuthRepository =
            instance ?: synchronized(this) {
                instance ?: UserAuthRepository(userPreference, apiService)
            }.also { instance = it }
    }
}