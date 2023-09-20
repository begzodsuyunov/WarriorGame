package com.estgame.warriorroad.fragments

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.estgame.warriorroad.MainActivity
import com.estgame.warriorroad.R

class SettingsFragment : Fragment() {
    private var imageView: ImageView? = null
    private var musicOn: Boolean = true // Default state is music on
    private var musicImageView: ImageView? = null
    private var vibratorImageView: ImageView? = null
    private var mediaPlayer: MediaPlayer? = null // MediaPlayer instance
    private lateinit var mainActivity: MainActivity
    private val MUSIC_STATE_KEY = "music_state_key" // Key for storing music state in SharedPreferences
    private var vibrationOn: Boolean = true // Default state is vibration on

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imageView = view.findViewById(R.id.backButtonToMenu)
        mediaPlayer = mainActivity.getMediaPlayer()

        musicImageView = view.findViewById(R.id.soundStatus)
        vibratorImageView = view.findViewById(R.id.vibrationStatus)

        // Set the initial music state based on SharedPreferences
        musicOn = isMusicOn()
        vibrationOn = isVibrationEnabled()
        updateVibrationImageView()

        updateMusicImageView()

        imageView!!.setOnClickListener {
            // Navigate back to the main fragment
            val action = SettingsFragmentDirections.actionSettingsFragmentToFragmentMain()
            findNavController().navigate(action)
        }

        musicImageView?.setOnClickListener {
            toggleMusicState()
        }
        vibratorImageView?.setOnClickListener {
            // Toggle the vibration setting
            vibrationOn = !vibrationOn

            // Save the vibration setting to SharedPreferences
            saveVibrationSetting(vibrationOn)

            // Update the ImageView to reflect the new state
            updateVibrationImageView()

            // Perform any necessary actions for enabling or disabling vibration
            if (vibrationOn) {
                // Vibration is enabled
            } else {
                // Vibration is disabled
            }
        }

    }

    private fun isVibrationEnabled(): Boolean {
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("vibration_enabled", true) // Default to true if the preference doesn't exist
    }

    private fun saveVibrationSetting(enabled: Boolean) {
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("vibration_enabled", enabled)
        editor.apply()
    }

    private fun updateVibrationImageView() {
        if (vibrationOn) {
            // Vibration is enabled, set the ImageView to "vibration_on"
            vibratorImageView?.setImageResource(R.drawable.tickbutton)
        } else {
            // Vibration is disabled, set the ImageView to "vibration_off"
            vibratorImageView?.setImageResource(R.drawable.disablebutton)
        }
    }
    private fun isMusicOn(): Boolean {
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(MUSIC_STATE_KEY, true) // Default to true if the preference doesn't exist
    }
    private fun toggleMusicState() {
        // Toggle the music state
        musicOn = !musicOn
        mainActivity.setMusicState(musicOn)
        saveMusicState()
        updateMusicImageView()
    }
    private fun updateMusicImageView() {
        if (musicOn) {
            // Video is playing, set the ImageView to "tick"
            musicImageView?.setImageResource(R.drawable.tickbutton)
        } else {
            // Video is not playing, set the ImageView to "disablebutton"
            musicImageView?.setImageResource(R.drawable.disablebutton)
        }
    }
    private fun loadMusicState() {
        // Load the music state from SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        musicOn = sharedPrefs.getBoolean(MUSIC_STATE_KEY, true)

        // Check if mediaPlayer is null or not initialized
        if (mediaPlayer != null) {
            if (musicOn && !mediaPlayer!!.isPlaying) {
                try {
                    mediaPlayer!!.start()
                } catch (e: IllegalStateException) {
                    // Handle any exceptions that may occur
                }
            } else if (!musicOn && mediaPlayer!!.isPlaying) {
                try {
                    mediaPlayer!!.pause()
                } catch (e: IllegalStateException) {
                    // Handle any exceptions that may occur
                }
            }
        }
    }
    private fun saveMusicState() {
        // Save the music state to SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(MUSIC_STATE_KEY, musicOn)
        editor.apply()
    }

}