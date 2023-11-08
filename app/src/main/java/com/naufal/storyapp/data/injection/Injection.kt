package com.naufal.storyapp.data.injection

import android.content.Context
import com.naufal.storyapp.data.database.UserAuthPreference
import com.naufal.storyapp.data.database.dataStore
import com.naufal.storyapp.data.repository.UserAuthRepository

object Injection {
    fun provideRepository(context: Context): UserAuthRepository {
        val pref = UserAuthPreference.getInstance(context.dataStore)
        return UserAuthRepository.getInstance(pref)
    }
}