package com.example.podwave.ui.activity

import com.example.podwave.ui.adapter.EpisodesAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.podwave.R
import com.example.podwave.data.model.Episode
import com.example.podwave.data.model.Podcast
import com.example.podwave.ui.fragment.PlayerFragment
import com.example.podwave.ui.fragment.SimplePlayerFragment
import com.example.podwave.util.SharedPreferences
import com.example.podwave.util.UIUtil

@Suppress("DEPRECATION")
class PodcastActivity : AppCompatActivity() {
    private lateinit var podcastImage: ImageView
    private lateinit var podcastTitle: TextView
    private lateinit var podcastCategory: TextView
    private lateinit var podcastAuthor: TextView
    private lateinit var podcastDescription: TextView
    private lateinit var podcastEpisodeSize: TextView
    lateinit var episodesRecyclerView: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    var currentEpisodeIndex: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitivty_podcast)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initVariables()

        val podcast = sharedPreferences.getPodcast()

        initPodcast(podcast)

    }

    private fun initPodcast(podcast: Podcast?) {
        if (podcast != null) {
            changePodcast(podcast)
            createEpisodesRecyclerView(podcast)
        } else {
            UIUtil.showDialog(
                context = this,
                title = getString(R.string.dialog_tittle_cache),
                message = getString(R.string.dialog_description_back),
                titleColor = String.format(
                    "#%06X",
                    0xFFFFFF and ContextCompat.getColor(this, R.color.red)
                ),
                closeButtonText = getString(R.string.dialog_button_back),
                showOkButton = false,
                showCloseButton = true,
                onCloseClick = {
                    runOnUiThread {
                        startActivity(Intent(this, MainActivity::class.java))
                        Animatoo.animateSlideRight(this)
                        finish()
                    }
                },
            )
        }
    }

    private fun changePodcast(podcast: Podcast) {
        podcastTitle.text = podcast.title
        podcastCategory.text = podcast.category ?: getString(R.string.podcast_category_class)
        podcastAuthor.text = podcast.author ?: getString(R.string.podcast_autor_class)
        podcastDescription.text = podcast.description
        podcastEpisodeSize.text = "Episódios: ${podcast.episodes.size}"


        Glide.with(this)
            .load(podcast.imageUrl ?: R.drawable.ic_launcher_background)
            .apply(RequestOptions().transform(RoundedCorners(10)))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_launcher_background)
            .into(podcastImage)
    }

    private fun createEpisodesRecyclerView(podcast: Podcast) {
        episodesRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = EpisodesAdapter(this,
            podcast.episodes,
            onPlayClick = { selectedIndex ->
                openSimplePlayerFragment(podcast.episodes, selectedIndex)
            },
            onItemClick = { selectedIndex ->
                openPlayerFragment(podcast.episodes, selectedIndex)
            }
        )
        episodesRecyclerView.adapter = adapter

        adapter.setPlayingIndex(currentEpisodeIndex)
    }
    //▶️▶️
    private fun initVariables() {
        sharedPreferences = SharedPreferences(this)

        podcastImage = findViewById(R.id.podcast_image_layout)
        podcastTitle = findViewById(R.id.podcast_title_layout)
        podcastCategory = findViewById(R.id.podcast_category_layout)
        podcastAuthor = findViewById(R.id.podcast_author_layout)
        podcastDescription = findViewById(R.id.podcast_description_layout)
        podcastEpisodeSize = findViewById(R.id.podcast_episode_size_layout)
        episodesRecyclerView = findViewById(R.id.episodes_recycler_view_layout)
    }

    //➡️➡️
    private fun openPlayerFragment(episodes: List<Episode>, currentIndex: Int) {
        val fragment = PlayerFragment.newInstance(episodes, currentIndex)
        fragment.show(supportFragmentManager, "PlayerFragment")
    }

    //➡️➡️
    private fun openSimplePlayerFragment(episodes: List<Episode>, currentIndex: Int) {
        currentEpisodeIndex = currentIndex
        val fragment = SimplePlayerFragment.newInstance(episodes, currentIndex)
        fragment.show(supportFragmentManager, "SimplePlayerFragment")
        (episodesRecyclerView.adapter as? EpisodesAdapter)?.setPlayingIndex(currentEpisodeIndex)
    }

    //⬅️⬅️
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            findViewById<FrameLayout>(R.id.fragment_container_layout).visibility = View.GONE
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            Animatoo.animateSlideRight(this)
            finish()
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                startActivity(Intent(this, MainActivity::class.java))
                Animatoo.animateSlideRight(this)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}