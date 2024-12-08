package com.example.podwave.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.podwave.R
import com.example.podwave.data.model.Episode
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class PlayerFragment : BottomSheetDialogFragment(){


    private var exoplayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var episodeImage: ImageView
    private lateinit var episodeTitle: TextView
    private var episodeList: List<Episode> = emptyList()
    private var currentEpisodePosition: Int = 0

    companion object {
        private const val EPISODES_LIST = "episodes_list"
        private const val CURRENT_INDEX = "current_index"

        fun newInstance(episodes: List<Episode>, currentIndex: Int): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putSerializable(EPISODES_LIST, ArrayList(episodes))
            args.putInt(CURRENT_INDEX, currentIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables(view)

        episodeList = arguments?.getSerializable(EPISODES_LIST) as? List<Episode> ?: emptyList()
        currentEpisodePosition = arguments?.getInt(CURRENT_INDEX) ?: 0

        if (episodeList.isNotEmpty()) {
            player(currentEpisodePosition)
        }
    }
    //▶️▶️
    private fun initVariables(view: View) {
        playerView = view.findViewById(R.id.player)
        episodeImage = view.findViewById(R.id.episode_image)
        episodeTitle = view.findViewById(R.id.episode_title)
    }
    //🎼🎼
    private fun player(currentIndex: Int) {
        val currentEpisode = episodeList[currentIndex]

        updateEpisode(currentEpisode)

        exoplayer = ExoPlayer.Builder(requireContext()).build().apply {
            playerView.player = this

            episodeList.forEach { episode ->
                val mediaItem = MediaItem.fromUri(Uri.parse(episode.audioUrl))
                addMediaItem(mediaItem)
            }

            seekTo(currentIndex, 0)
            prepare()
            play()

            addListener(object : Player.Listener {
                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)

                    val currentMediaIndex = currentMediaItemIndex
                    if (currentMediaIndex in episodeList.indices) {
                        val newEpisode = episodeList[currentMediaIndex]
                        updateEpisode(newEpisode)
                    }
                }
            })
        }
    }
    //🔄️🔄️
    private fun updateEpisode(episode: Episode) {
        episodeTitle.text = episode.title
        Glide.with(this)
            .load(episode.imageUrl)
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .into(episodeImage)
    }
    //💬💬
    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = false
            }
        }
        return dialog
    }
    //⏹️⏹️
    override fun onStop() {
        super.onStop()
        exoplayer?.release()
        exoplayer = null
    }

}