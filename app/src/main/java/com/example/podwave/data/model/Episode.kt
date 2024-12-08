package com.example.podwave.data.model

import java.io.Serializable

class Episode(
    val title: String,
    val description: String,
    val audioUrl: String,
    val imageUrl: String?,
    val pubDate: String
) : Serializable
