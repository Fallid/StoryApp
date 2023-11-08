package com.naufal.storyapp.data.repository

import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.database.UserModelAuth
import kotlinx.coroutines.flow.Flow

class UserAuthRepository private constructor (private val userPreference: UserAuthPreference) {
    suspend fun saveSession(user: UserModelAuth) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModelAuth> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
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