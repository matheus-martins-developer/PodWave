import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.podwave.R
import com.example.podwave.data.model.Episode

class EpisodesAdapter(
    private val episodes: List<Episode>,
    applicationContext: Context,
    private val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<EpisodesAdapter.EpisodeViewHolder>() {

    inner class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val view2: View = view
        private val title: TextView = view.findViewById(R.id.episode_title_layout)
        private val pubDate: TextView = view.findViewById(R.id.episode_date_layout)
        private val imageEpidose: ImageView = view.findViewById(R.id.image_episode_layout)

        fun bind(episode: Episode, position: Int) {
            title.text = episode.title
            pubDate.text = formatPubDate(episode.pubDate)
            Glide.with(view2)
                .load(episode.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(imageEpidose)

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
}
