package com.eliorcohen12345.locationproject.DataAppPackage

import java.util.*

data class ChatMessage(
        val text: String,
        val email: String,
        val user: String,
        val timestamp: Date
)