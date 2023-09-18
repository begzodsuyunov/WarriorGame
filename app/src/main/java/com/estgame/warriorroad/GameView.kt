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
import android.telephony.BarringInfo
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Random

class GameView(context: Context, screenX: Int, screenY: Int) : SurfaceView(context), Runnable {
    private var isPlaying: Boolean = false
    private var thread: Thread? = null;
    private val background1 = Background(screenX, screenY, resources)
    private val background2 = Background(screenX, screenY, resources)
    private var screenX = screenX
    private var screenY = screenY
    private var paint: Paint
    private var sword: Sword
    private var coin: Coin

    //    private var barrier: Barrier? = null
    private val barriers = ArrayList<Barrier>()
    private var barrier1: Barrier? = null
    private var barrier2: Barrier? = null
    private var barrier3: Barrier? = null
    private var barrier4: Barrier? = null

    private val coins = ArrayList<Coin>()

    companion object {
        var screenRatioY: Float? = null
        var screenRatioX: Float? = null

    }

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
        sword = Sword(screenY, resources)

        barrier1 = Barrier(screenX / 6, screenY - sword.height * 2, resources)
        barrier2 = Barrier(screenX / 3, screenY - sword.height * 2, resources)
        barrier3 = Barrier(screenX / 2, screenY - sword.height * 2, resources)
        barrier4 = Barrier(screenX * 2 / 3, screenY - sword.height * 2, resources)

        coin = Coin(resources)
        coin.x = (screenX / 2 - coin.width / 2) // Center the coin horizontally
        coin.y = barrier2!!.y - coin.height - 20 // Place the coin just above barrier2
        coins.add(coin)
        paint = Paint()
        val coinSpacing = 50 // Adjust the vertical spacing between each coin
        val coinYOffset = -20 // Offset to place the coin just above the barrier

        for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
            val coinX = barrier!!.x + (barrier.width / 2) - (coin.width / 2) // Center the coin horizontally on the barrier
            val coinY = barrier.y + coinYOffset // Place the coin just above the barrier
            coin.x = coinX
            coin.y = coinY
        }

//        paint.textSize = 128f
//        paint.color = Color.WHITE
//        birds = Array(4) { Bird(resources) }
//        for (i in birds.indices) {
//            birds[i] = Bird(resources)
//        }
    }

    override fun run() {

        while (isPlaying) {
            update()
            draw()
            sleep()
        }
    }

    private fun update() {
        // Update the x positions of both backgrounds
        sword.update()

        background1.x -= (6 * screenRatioX!!).toInt()
        background2.x -= (6 * screenRatioX!!).toInt()


        // Check if background1 has reached the end of the screen
        if (background1.x + background1.background.width < 0) {
            background1.x = background2.x + background2.background.width
        }

        // Check if background2 has reached the end of the screen
        if (background2.x + background2.background.width < 0) {
            background2.x = background1.x + background1.background.width
        }

        if (sword.isForward) {
//            background1.x -= (15 * screenRatioX!!).toInt()
//            background2.x -= (15 * screenRatioX!!).toInt()
            sword.x += (5 * screenRatioX!!).toInt()
        } else {
            sword.x -= (4 * screenRatioX!!).toInt()
        }
        // Update barrier positions based on the background positions
        barrier1!!.x = background1.x + (screenX / 6) + 40
        barrier1!!.y = screenY - (sword.height * 2.7).toInt()  // Adjust the y-coordinate as needed
        barrier2!!.x = background1.x + (screenX * 1.4).toInt()
        barrier2!!.y = screenY - (sword.height * 3.8).toInt()  // Adjust the y-coordinate as needed
        barrier3!!.x = background2.x + (screenX - 350).toInt()
        barrier3!!.y = screenY - (sword.height * 3.5).toInt()  // Adjust the y-coordinate as needed
        barrier4!!.x = background2.x + (screenX + 400)
        barrier4!!.y = screenY - (sword.height * 2.3).toInt()  // Adjust the y-coordinate as needed
        // Check for collisions between sword and barriers and adjust position
        val swordRect = sword.getCollisionShape()

        for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
            if (Rect.intersects(barrier!!.getCollisionShape(), sword.getCollisionShape())) {
                val barrierTop = barrier.getCollisionShape().top
                val barrierBottom = barrier.getCollisionShape().bottom
                val barrierLeft = barrier.getCollisionShape().left
                val barrierRight = barrier.getCollisionShape().right
                val swordTop = sword.getCollisionShape().top
                val swordBottom = sword.getCollisionShape().bottom
                val swordLeft = sword.getCollisionShape().left
                val swordRight = sword.getCollisionShape().right

                // Check if sword is above the barrier and not overlapping horizontally
                if (swordBottom >= barrierTop && swordTop <= barrierTop && swordLeft >= barrierLeft - 20) {
                    // Sword is on top of the barrier, allow it to walk
                    sword.y = barrierTop - sword.height + 20
                } else {
                    // Sword is colliding with the left or right part of the barrier, prevent movement
                    sword.isForward = false
                }
            }
        }

        val coinsToRemove = ArrayList<Coin>()

        for (coin in coins) {
            val coinRect = coin.getCollisionShape()

            if (Rect.intersects(coinRect, swordRect)) {
                // Collision detected, add the coin to the list for removal
                coinsToRemove.add(coin)
            }
        }
        coins.removeAll(coinsToRemove)

//        if (sword.isForward && !swordOnBarrier) {
//            // Move the sword forward if it's not on a barrier
//            background1.x -= (15 * screenRatioX!!).toInt()
//            background2.x -= (15 * screenRatioX!!).toInt()
//            sword.x += (2 * screenRatioX!!).toInt() // You can adjust the sword's forward speed as needed
//        }

//        if (sword.isJump) {
//            sword.y -= (23 * screenRatioY!!).toInt()
//        } else {
//            sword.y += (10 * screenRatioY!!).toInt()
//        }

        if (sword.x < 0)
            sword.x = 0
        if (sword.x > screenX - sword.width)
            sword.x = screenX - sword.width
    }


    private fun draw() {
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(
                background1.background,
                null,
                Rect(
                    background1.x,
                    background1.y,
                    background1.x + background1.background.width,
                    background1.y + background1.background.height
                ),
                paint
            )
            canvas.drawBitmap(
                background2.background,
                null,
                Rect(
                    background2.x,
                    background2.y,
                    background2.x + background2.background.width,
                    background2.y + background2.background.height
                ),
                paint
            )
            canvas.drawBitmap(
                barrier1!!.barrierBitmap,
                barrier1!!.x.toFloat(),
                barrier1!!.y.toFloat(),
                paint
            )
            canvas.drawBitmap(
                barrier2!!.barrierBitmap,
                barrier2!!.x.toFloat(),
                barrier2!!.y.toFloat(),
                paint
            )
            canvas.drawBitmap(
                barrier3!!.barrierBitmap,
                barrier3!!.x.toFloat(),
                barrier3!!.y.toFloat(),
                paint
            )
            canvas.drawBitmap(
                barrier4!!.barrierBitmap,
                barrier4!!.x.toFloat(),
                barrier4!!.y.toFloat(),
                paint
            )
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
            canvas.drawBitmap(sword.sword, sword.x.toFloat(), sword.y.toFloat(), paint)
            // Draw the coin for each barrier
            for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
                val coinX = barrier!!.x + (barrier.width / 2) - (coin.width / 2)
                val coinY = barrier.y - coin.height // Place the coin just above the barrier
                canvas.drawBitmap(coin.coin, coinX.toFloat(), coinY.toFloat(), paint)
            }
//            for (bullet in bullets) {
//                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint)
//            }
            holder.unlockCanvasAndPost(canvas)
        }

    }

    private fun sleep() {
        try {
            Thread.sleep(17)

        } catch (e: Error) {
            println(e)
        }
    }

    fun resume() {
        try {
            isPlaying = true
            thread = Thread(this)
            thread!!.start()
        } catch (e: Error) {
            println(e)
        }


    }

    fun pause() {
        isPlaying = false
        thread?.join()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (event.y > 3 * screenY / 4) {
                        sword.isForward = true
                    } else {
                        sword.jump()

                    }
                }

                MotionEvent.ACTION_UP -> {
                    sword.isForward = false
                    //                if (event.x > screenX / 2) {
                    //                    sword.toShoot++
                    //                }
                }
            }
        }
        return true

    }

}