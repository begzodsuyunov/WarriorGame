package com.estgame.warriorroad.fragments

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.navigation.fragment.findNavController
import com.estgame.warriorroad.GameView
import com.estgame.warriorroad.R


class GameFragment : Fragment() {
    private lateinit var gameView: GameView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        windowManager.defaultDisplay.getSize(point)

        gameView = GameView(requireContext(), point.x, point.y)


        val layoutParams = LayoutParams()
        layoutParams.width = LayoutParams.MATCH_PARENT
        layoutParams.height = LayoutParams.MATCH_PARENT

        (view as ViewGroup).addView(gameView, layoutParams)

        gameView.setGameOverListener(object : GameView.GameOverListener {
            override fun onGameOver(score: Int) {
                val action = GameFragmentDirections.actionGameFragmentToGameOverFragment(score)
                requireActivity().runOnUiThread {
                    findNavController().navigate(action)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        gameView.resume()
    }
}