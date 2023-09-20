package com.estgame.warriorroad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.estgame.warriorroad.R



class GameOverFragment : Fragment() {

    private var score: Int = 0
    private var scoreText: TextView? = null
    private var imageView: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_over, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            score = it.getInt("score", 0)
        }
        scoreText = view.findViewById(R.id.gameOverScore) // Initialize scoreText here
        imageView = view.findViewById(R.id.backButtonToMenu)
        scoreText?.text = score.toString() // Set the score text
        println("newscree")

        imageView!!.setOnClickListener {
            // Navigate back to the main fragment
            val action = GameOverFragmentDirections.actionGameOverFragmentToFragmentMain()
            findNavController().navigate(action)
        }
    }

}