package com.example.podwave.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.podwave.R
import com.example.podwave.data.model.Episode
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

@Suppress("DEPRECATION")
class PlayerFragment : BottomSheetDialogFragment(){

    private lateinit var player: PlayerView
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

        }
    }
    //▶️▶️
    private fun initVariables(view: View) {
        player = view.findViewById(R.id.player)
        episodeImage = view.findViewById(R.id.episode_image)
        episodeTitle = view.findViewById(R.id.episode_title)
    }
}