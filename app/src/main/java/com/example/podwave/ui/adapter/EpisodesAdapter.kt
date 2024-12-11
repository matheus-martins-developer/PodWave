package com.example.podwave.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.podwave.R
import com.example.podwave.data.model.Episode

class EpisodesAdapter(
    private val context: Context,
    private val episodes: List<Episode>,
    private val onPlayClick: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    private var currentPlayingIndex: Int? = null

    inner class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val view2: View = view
        private val play: ImageView = view.findViewById(R.id.play_layout)
        private val title: TextView = view.findViewById(R.id.episode_title_layout)
        private val pubDate: TextView = view.findViewById(R.id.episode_date_layout)
        private val imageEpisode: ImageView = view.findViewById(R.id.image_episode_layout)
        val lottieLoading: LottieAnimationView = view.findViewById(R.id.image_loading)


        fun bind(episode: Episode, position: Int) {
            title.text = episode.title
            pubDate.text = context.getString(R.string.episode_date_text) + " " + formatPubDate(episode.pubDate)

            lottieLoading.visibility = View.VISIBLE
            imageEpisode.visibility = View.INVISIBLE

            Glide.with(view2)
                .load(episode.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions().transform(RoundedCorners(10)))
                .error(R.mipmap.ic_launcher)
                .listener(object :
                    com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieLoading.visibility = View.GONE
                        imageEpisode.visibility = View.VISIBLE
                        imageEpisode.setImageResource(R.mipmap.ic_launcher)
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        lottieLoading.visibility = View.GONE
                        imageEpisode.visibility = View.VISIBLE
                        return false
                    }
                })
                .into(imageEpisode)

            val isPlaying = position == currentPlayingIndex

            Glide.with(play.context)
                .load(if (isPlaying) R.drawable.btn_pause else R.drawable.btn_play)
                .into(play)

            play.setOnClickListener {
                onPlayClick(position)
                updatePlayingState(position)
            }

            itemView.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(episodes[position], position)
    }

    override fun getItemCount(): Int = episodes.size

    //📅📅
    private fun formatPubDate(pubDate: String): String {
        return try {
            val inputFormat =
                java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", java.util.Locale.US)
            val outputFormat =
                java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val date = inputFormat.parse(pubDate)
            outputFormat.format(date ?: java.util.Date())
        } catch (e: Exception) {
            pubDate
        }
    }

    //🔄️🔄️
    private fun updatePlayingState(newPlayingIndex: Int) {
        val previousIndex = currentPlayingIndex
        currentPlayingIndex = newPlayingIndex

        previousIndex?.let { notifyItemChanged(it) }
        notifyItemChanged(newPlayingIndex)
    }

    //🔄️🔄️
    fun setPlayingIndex(index: Int?) {
        val previousIndex = currentPlayingIndex
        currentPlayingIndex = index

        previousIndex?.let { notifyItemChanged(it) }
        currentPlayingIndex?.let { notifyItemChanged(it) }
    }
}
