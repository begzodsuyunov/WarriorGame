package com.estgame.warriorroad

import android.content.Context
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.estgame.warriorroad.fragments.HomeFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private var mediaPlayer: MediaPlayer? = null
    private var isMusicPaused = false
    private var isMusicOn = true
    private val MUSIC_STATE_KEY = "music_state_key"
    private val MAIN_MUSIC_STATE_KEY = "main_music_state_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize the NavController with the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize MediaPlayer and start playing music here
        mediaPlayer = MediaPlayer.create(this, R.raw.allscreensound)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        isMusicOn = sharedPrefs.getBoolean(MUSIC_STATE_KEY, true)
        setMusicState(isMusicOn)
    }

    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer!!
    }

    fun setMusicState(musicOn: Boolean) {
        this.isMusicOn = musicOn
        if (isMusicOn && !isMusicPaused) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }

        // Save the music state to SharedPreferences
        saveMusicState(isMusicOn)

        // Update the UI based on the music state
        // You can implement this logic if needed
    }
    private fun saveMusicState(musicOn: Boolean) {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean(MUSIC_STATE_KEY, musicOn)
        editor.apply()
    }
    private fun clearAllPreferences() {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear()
        editor.apply()
    }
    override fun onPause() {
        super.onPause()
// Pause the music when the activity goes into the background
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isMusicPaused = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    override fun onResume() {
        super.onResume()
// Resume the music when the activity comes back to the foreground
        if (isMusicPaused) {
            mediaPlayer?.start()
            isMusicPaused = false
        }
    }


    // Override the onBackPressed method to handle "Up" navigation
    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

}