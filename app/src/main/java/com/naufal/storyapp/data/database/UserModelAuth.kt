package com.naufal.storyapp.data.database

data class UserModelAuth (
    val idUser: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)