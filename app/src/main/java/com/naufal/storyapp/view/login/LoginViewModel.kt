package com.naufal.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.storyapp.data.database.UserModelAuth
import com.naufal.storyapp.data.repository.UserAuthRepository
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserAuthRepository) : ViewModel() {
    fun saveSession(user: UserModelAuth) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}