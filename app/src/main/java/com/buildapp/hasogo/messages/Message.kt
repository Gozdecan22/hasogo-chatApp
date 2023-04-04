package com.buildapp.hasogo.messages

data class Message(
    var messageId: String = "",
    var message: String = "",
    var senderId: String = "",
    var timeStamp: Long = 0L,
    var feeling: Int = 0,
    var imageUrl: String = "",
)
