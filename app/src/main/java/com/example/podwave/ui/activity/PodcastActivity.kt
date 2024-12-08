package com.example.podwave.ui.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.podwave.R
import com.example.podwave.data.model.Podcast

@Suppress("DEPRECATION")
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
        val podcast = intent.getSerializableExtra("PODCAST") as? Podcast

        if (podcast != null) {
            changePodcast(podcast)
        } else {
            Toast.makeText(this, R.string.parse_null, Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun changePodcast(podcast: Podcast) {
        podcastTitle.text = podcast.title
        podcastCategory.text = podcast.category ?: "Categoria desconhecida"
        podcastAuthor.text = podcast.author ?: "Autor desconhecido"
        podcastDescription.text = podcast.description

        Glide.with(this)
            .load(podcast.imageUrl ?: R.drawable.ic_launcher_background)
            .placeholder(R.drawable.ic_launcher_background)
            .into(podcastImage)
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