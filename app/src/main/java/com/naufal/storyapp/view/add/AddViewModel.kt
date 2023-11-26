package com.naufal.storyapp.view.add

import androidx.lifecycle.ViewModel
import com.naufal.storyapp.data.repository.UserAuthRepository
import okhttp3.MultipartBody
import java.io.File

class AddViewModel(private val repository: UserAuthRepository) :ViewModel(){
    fun addStory(
        description: String,
        imageFile: File,
        latitude: Double,
        longitude: Double
    ) = repository.addStory(description,imageFile, latitude, longitude)
}