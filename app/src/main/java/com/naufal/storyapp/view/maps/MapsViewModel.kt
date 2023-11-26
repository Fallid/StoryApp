package com.naufal.storyapp.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.repository.ResultProcess
import com.naufal.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class MapsViewModel(private val preference: UserAuthPreference):ViewModel() {

    fun getStoriesWithLocation() = liveData {
        emit(ResultProcess.Loading)
        try {
            val user = runBlocking { preference.getSession().first() }
            val apiService = ApiConfig.getApiService(user.token)
            val mapsResponse = apiService.getLocation()
            emit(ResultProcess.Success(mapsResponse.listStory))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorMessage = jsonInString.toString()
            emit(ResultProcess.Error(errorMessage))
        }
    }
}