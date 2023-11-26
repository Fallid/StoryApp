package com.naufal.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiConfig
import com.naufal.storyapp.data.retrofit.ApiService
import com.naufal.storyapp.view.paging.StoryPaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryRepository (private val apiService: ApiService, private val userAuthPreference: UserAuthPreference) {


    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val user = runBlocking { userAuthPreference.getSession().first() }
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
        private var instance: StoryRepository? = null

        fun getInstance(
            userPreference: UserAuthPreference,
            apiService: ApiService
        ): StoryRepository =
            StoryRepository.instance ?: synchronized(this) {
                StoryRepository.instance ?: StoryRepository(apiService, userPreference)
            }.also { StoryRepository.instance = it }
    }
}