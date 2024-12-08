package com.example.podwave.data.model

import java.io.Serializable

class Podcast(
    val title: String,
    val description: String,
    val link: String,
    val imageUrl: String?,
    val author: String?,
    val category: String?,
    val episodes: List<Episode>
) : Serializable
