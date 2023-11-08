package com.naufal.storyapp.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserAuthPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModelAuth) {
        dataStore.edit { preferences ->
            preferences[IDUSER_KEY] = user.idUser
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModelAuth> {
        return dataStore.data.map { preferences ->
            UserModelAuth(
                preferences[IDUSER_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserAuthPreference? = null

        private val IDUSER_KEY = stringPreferencesKey("idUser")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        fun getInstance(dataStore: DataStore<Preferences>): UserAuthPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserAuthPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}