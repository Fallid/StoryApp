package com.naufal.storyapp.data.repository

sealed class ResultProcess<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultProcess<T>()
    data class Error(val error: String) : ResultProcess<Nothing>()
    object Loading : ResultProcess<Nothing>()
}