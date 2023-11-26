package com.naufal.storyapp.view.maps

import androidx.lifecycle.ViewModel
import com.naufal.storyapp.data.repository.UserAuthRepository

class MapsViewModel(private val repository: UserAuthRepository):ViewModel() {
    fun getLocation() = repository.getLocation()

}