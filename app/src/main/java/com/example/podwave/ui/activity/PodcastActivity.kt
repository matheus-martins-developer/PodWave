package com.example.podwave.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.podwave.R

class PodcastActivity : AppCompatActivity() {
    private lateinit var podcastImage: ImageView
    private lateinit var podcastTitle: TextView
    private lateinit var podcastCategory: TextView
    private lateinit var podcastAuthor: TextView
    private lateinit var podcastDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.podcast_activity)

        initVariables()

    }

    //▶️▶️
    private fun initVariables() {
        podcastImage = findViewById(R.id.podcast_image)
        podcastTitle = findViewById(R.id.podcast_title)
        podcastCategory = findViewById(R.id.podcast_category)
        podcastAuthor = findViewById(R.id.podcast_author)
        podcastDescription = findViewById(R.id.podcast_description)
    }
}