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
import com.naufal.storyapp.view.paging.StoryPaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class UserAuthRepository private constructor(
    private val userPreference: UserAuthPreference
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

    fun getLocation() = liveData {
        emit(ResultProcess.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val mapsResponse = apiService.getLocation()
            emit(ResultProcess.Success(mapsResponse.listStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorMessage = jsonInString.toString()
            emit(ResultProcess.Error(errorMessage))
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
            userPreference: UserAuthPreference
        ): UserAuthRepository =
            instance ?: synchronized(this) {
                instance ?: UserAuthRepository(userPreference)
            }.also { instance = it }
    }
}