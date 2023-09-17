package com.estgame.warriorroad.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.estgame.warriorroad.R



class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameText = view.findViewById<TextView>(R.id.gameText)
        gameText.setOnClickListener {
            // Navigate to the new fragment when "game" text is clicked
            findNavController().navigate(R.id.action_fragment_main_to_gameFragment)
        }
    }
}