package com.naufal.storyapp.data.repository

import com.naufal.storyapp.data.response.story.ListStoryItem
import com.naufal.storyapp.data.retrofit.ApiService

class StoryRepository (private val apiService: ApiService) {

    suspend fun getStories(token: String, onSuccess: (List<ListStoryItem>) -> Unit, onError: (String) -> Unit) {
        try {
            val response = apiService.getStories("Bearer $token")
            if (response.error == true) {
                response.message?.let { onError(it) }
            } else {
                onSuccess(response.listStory)
            }
        } catch (e: Exception) {
            onError(e.message ?: "Unknown error")
        }
    }
    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also { instance = it }
            }
    }
}