package com.estgame.warriorroad

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.provider.SyncStateContract.Helpers.update
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context, screenX: Int, screenY: Int) : SurfaceView(context), Runnable {
    private var isPlaying: Boolean = false
    private var thread: Thread? = null;
    private val background1 = Background(screenX, screenY, resources)
    private val background2 = Background(screenX, screenY, resources)
    private var screenX = screenX
    private var screenY = screenY
    private lateinit var paint: Paint
    private var screenRatioX: Float? = null
    private var screenRatioY: Float? = null

    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val audioAttributes = AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_GAME)
//                .build()
//            soundPool = SoundPool.Builder()
//                .setAudioAttributes(audioAttributes)
//                .build()
//        } else {
//            soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
//        }
//        sound = soundPool.load(activity, R.raw.shoot, 1)
        screenRatioX = 1920f / screenX
        screenRatioY = 1080f / screenY
        background2.x = screenX * 2 // Set the initial x position of background2
        paint = Paint()
//        paint.textSize = 128f
//        paint.color = Color.WHITE
//        birds = Array(4) { Bird(resources) }
//        for (i in birds.indices) {
//            birds[i] = Bird(resources)
//        }
    }
    override fun run() {

        while(isPlaying){
            update()
            draw()
            sleep()
        }
    }

    private fun update() {
        // Update the x positions of both backgrounds
        background1.x -= (5 * screenRatioX!!).toInt()
        background2.x -= (5 * screenRatioX!!).toInt()

        // Check if background1 has reached the end of the screen
        if (background1.x + background1.background.width < 0) {
            background1.x = background2.x + background2.background.width
        }

        // Check if background2 has reached the end of the screen
        if (background2.x + background2.background.width < 0) {
            background2.x = background1.x + background1.background.width
        }
    }


    private fun draw(){
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, null, Rect(background1.x, background1.y, background1.x + background1.background.width, background1.y + background1.background.height), paint)
            canvas.drawBitmap(background2.background, null, Rect(background2.x, background2.y, background2.x + background2.background.width, background2.y + background2.background.height), paint)

//            for (bird in birds) {
//                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint)
//            }
//            canvas.drawText(score.toString(), (screenX / 2).toFloat(), 164f, paint)
//            if (isGameOver) {
//                isPlaying = false
//                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint)
//                holder.unlockCanvasAndPost(canvas)
//                saveIfHighScore()
//                waitBeforeExiting()
//                return
//            }
//            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint)
//            for (bullet in bullets) {
//                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint)
//            }
            holder.unlockCanvasAndPost(canvas)
        }

    }
    private fun sleep(){
        try {
            Thread.sleep(17)

        } catch (e: Error) {
            println(e)
        }
    }
    fun resume(){
        try {
            isPlaying = true
            thread = Thread(this)
            thread!!.start()
        } catch (e: Error) {
            println(e)
        }


    }

    fun pause(){
        isPlaying = false
        thread?.join()
    }

}