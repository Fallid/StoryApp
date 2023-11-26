package com.naufal.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.naufal.storyapp.data.database.UserModelAuth
import com.naufal.storyapp.data.repository.UserAuthRepository
import kotlinx.coroutines.launch

class MainViewModel (private val repository: UserAuthRepository) : ViewModel(){
    fun getSession(): LiveData<UserModelAuth> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory() = repository.getStories().cachedIn(viewModelScope)
}