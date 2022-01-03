package com.moralabs.chatapp.model

import com.google.firebase.firestore.FieldValue
import java.sql.Timestamp

data class Message(
    val chatId : String =  "",
    val message : String = "",
    val sentBy : String = ""
)
