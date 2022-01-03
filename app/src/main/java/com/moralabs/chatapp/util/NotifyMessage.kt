package com.moralabs.chatapp.util

interface NotifyMessage {
    fun onSuccess(msg : String)
    fun onFailure(msg : String?)
}