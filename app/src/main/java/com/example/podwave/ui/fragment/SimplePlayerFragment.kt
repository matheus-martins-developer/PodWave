package com.example.podwave.ui.fragment

import com.example.podwave.ui.adapter.EpisodesAdapter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.example.podwave.R
import com.example.podwave.data.model.Episode
import com.example.podwave.ui.activity.PodcastActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class SimplePlayerFragment : BottomSheetDialogFragment() {

    private var exoplayer: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var episodeList: List<Episode> = emptyList()
    private var currentEpisodePosition: Int = 0
    private lateinit var waves: LottieAnimationView

    companion object {
        private const val EPISODES_LIST = "episodes_list"
        private const val CURRENT_INDEX = "current_index"

        fun newInstance(episodes: List<Episode>, currentIndex: Int): SimplePlayerFragment {
            val fragment = SimplePlayerFragment()
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
        return inflater.inflate(R.layout.fragment_simple_player, container, false)
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
        playerView = view.findViewById(R.id.player_layout)
        waves = view.findViewById(R.id.waves_laout)
    }

    //🎼🎼
    private fun player(currentIndex: Int) {
        if (exoplayer == null) {
            exoplayer = ExoPlayer.Builder(requireContext()).build().apply {
                playerView.player = this
            }
        }

        exoplayer?.apply {
            clearMediaItems()
            episodeList.forEach { episode ->
                val mediaItem = MediaItem.fromUri(Uri.parse(episode.audioUrl))
                addMediaItem(mediaItem)
            }
            prepare()
            seekTo(currentIndex, 0)
            play()
        }

        exoplayer?.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                val newIndex = exoplayer?.currentMediaItemIndex ?: 0
                if (newIndex != currentEpisodePosition) {
                    currentEpisodePosition = newIndex
                    updateRecyclerViewState(playbackStopped = false)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                waves.visibility = if (isPlaying) View.VISIBLE else View.INVISIBLE

                if (!isPlaying) {
                    updateRecyclerViewState(playbackStopped = true)
                } else {
                    updateRecyclerViewState(playbackStopped = false)
                }
            }
        })
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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

    //🔄️🔄️
    private fun updateRecyclerViewState(playbackStopped: Boolean = false) {
        (activity as? PodcastActivity)?.let { activity ->
            val adapter = activity.episodesRecyclerView.adapter as? EpisodesAdapter
            if (playbackStopped) {
                activity.currentEpisodeIndex = null
            } else {
                activity.currentEpisodeIndex = currentEpisodePosition
            }
            adapter?.setPlayingIndex(activity.currentEpisodeIndex)
        }
    }
}
