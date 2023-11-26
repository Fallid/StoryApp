package com.naufal.storyapp.data.injection

import android.content.Context
import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.database.dataStore
import com.naufal.storyapp.data.repository.UserAuthRepository
import com.naufal.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserAuthRepository {
        val pref = UserAuthPreference.getInstance(context.dataStore)
        val session = runBlocking { pref.getSession().first() }
        val setToken = ApiConfig.getApiService(session.token)
        return UserAuthRepository.getInstance(pref, setToken)
    }
}