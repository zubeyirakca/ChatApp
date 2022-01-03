package com.moralabs.chatapp.model

import android.provider.ContactsContract

data class User(
    val userId : String = "",
    val userName : String = "",
    val userNickname: String = "",
    val userSurname : String = "",
    val userEmail : String = "",
    val userPicture : String = ""
)
