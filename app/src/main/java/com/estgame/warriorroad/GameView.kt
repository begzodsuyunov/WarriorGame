package com.estgame.warriorroad

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Handler
import android.os.Vibrator
import android.view.MotionEvent
import android.view.SurfaceView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.res.ResourcesCompat

class GameView(context: Context, screenX: Int, screenY: Int) : SurfaceView(context), Runnable {
    private var isPlaying: Boolean = false
    private var isGameOver: Boolean = false
    private var thread: Thread? = null;
    private val background1 = Background(screenX, screenY, resources)
    private val background2 = Background(screenX, screenY, resources)
    private var screenX = screenX
    private var screenY = screenY
    private var paint: Paint
    private var sword: Sword
    private var score: Score
    private var scoreText: Int = 0

    //    private var coin: Coin


    //    private var barrier: Barrier? = null
    private val barriers = ArrayList<Barrier>()
    private var barrier1: Barrier? = null
    private var barrier2: Barrier? = null
    private var barrier3: Barrier? = null
    private var barrier4: Barrier? = null

    private var coin1: Coin? = null
    private var coin2: Coin? = null
    private var coin3: Coin? = null
    private var coin4: Coin? = null
    private val coinAddHandler = Handler()
    private var lastRemovedCoin: Coin? = null
    private val coinsToAdd = ArrayList<Coin>()
    private var nextCoinToAdd: Coin? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    } else {
        null
    }
    private var vibrationOn: Boolean = true // Default state is vibration on

    private val coins = ArrayList<Coin>()
    private var lastCoinAdditionTime: Long = 0
    private val coinAdditionInterval = 4000L // Delay for 3000 milliseconds (3 seconds)
    private var gameOverListener: GameOverListener? = null

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
        score = Score(resources)

        barrier1 = Barrier(screenX / 6, screenY - sword.height * 2, resources)
        barrier2 = Barrier(screenX / 3, screenY - sword.height * 2, resources)
        barrier3 = Barrier(screenX / 2, screenY - sword.height * 2, resources)
        barrier4 = Barrier(screenX * 2 / 3, screenY - sword.height * 2, resources)

        coin1 = Coin(screenX / 6, screenY - sword.height * 2, resources)
        coin2 = Coin(screenX / 3, screenY - sword.height * 2, resources)
        coin3 = Coin(screenX / 2, screenY - sword.height * 2, resources)
        coin4 = Coin(screenX * 2 / 3, screenY - sword.height * 2, resources)

        coins.add(coin1!!)
        coins.add(coin2!!)
        coins.add(coin3!!)
        coins.add(coin4!!)
//        coin = Coin(resources)
////        coin.x = (screenX / 2 - coin.width / 2) // Center the coin horizontally
////        coin.y = barrier2!!.y - coin.height - 20 // Place the coin just above barrier2
//        coins.add(coin)
        paint = Paint()

    }

    interface GameOverListener {
        fun onGameOver(score: Int)
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
        coin1!!.x = background1.x + (screenX / 3.5).toInt() + 40
        coin1!!.y = screenY - (sword.height * 3).toInt()    // Adjust the y-coordinate as needed
        coin2!!.x = background1.x + (screenX * 1.55).toInt()
        coin2!!.y = screenY - (sword.height * 4.1).toInt()   // Adjust the y-coordinate as needed
        coin3!!.x = background2.x + (screenX * 0.8).toInt()
        coin3!!.y = screenY - (sword.height * 3.8).toInt()   // Adjust the y-coordinate as needed
        coin4!!.x = background2.x + (screenX * 1.5).toInt()
        coin4!!.y = screenY - (sword.height * 2.6).toInt()   // Adjust the y-coordinate as needed

        // Remove the last coin from the list

        for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
            if (Rect.intersects(barrier!!.getCollisionShape(), sword.getCollisionShape())) {
                val barrierRect = barrier.getCollisionShape()
                val swordRect = sword.getCollisionShape()

                // Calculate the collision points
                val swordRight = swordRect.right
                val swordTop = swordRect.top
                val swordBottom = swordRect.bottom
                val swordLeft = swordRect.left

                // Calculate the center point of the barrier's top edge
                val barrierTopCenterX = (barrierRect.left + barrierRect.right) / 2
                val barrierTopCenterY = barrierRect.top

                // Check if the sword is on top of the barrier's center
                val isOnBarrierCenter = swordTop <= barrierTopCenterY + 20
                        && swordRight >= barrierTopCenterX - sword.width


                if (isOnBarrierCenter) {
                    // Sword is on top of the barrier's center, allow it to walk
                    sword.y = barrierRect.top - sword.height + 20
                } else {
                    // Sword is colliding with other parts of the barrier, prevent movement
                    println("Game Over")
                    isGameOver = true
                    return
                }
            }
        }


        val coinsToRemove = ArrayList<Coin>()
        val currentTime = System.currentTimeMillis()

        for (coin in coins) {
            if (Rect.intersects(sword.getCollisionShape(), coin.getCollisionShape())) {
                coinsToRemove.add(coin)
                nextCoinToAdd = coin
                lastRemovedCoin = coin
                scoreText += 5
                lastCoinAdditionTime = currentTime

            }
        }

        coins.removeAll(coinsToRemove)
        // Check if there's a coin to be added and the delay has passed
        if (nextCoinToAdd != null && currentTime - lastCoinAdditionTime >= coinAdditionInterval) {
            coins.add(nextCoinToAdd!!)
            nextCoinToAdd = null
        }
//        var collidedBarrier: Barrier? = null
//        var collidedCoin: Coin? = null
//        val coinsToRemove = ArrayList<Coin>()
//
//
//
//        for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
//            for (coin in coins) { // Iterate through coins associated with the barrier
//                val coinRect = coin.getCollisionShape(barrier!!.x, barrier.y)
//                val swordRect = sword.getCollisionShape()
//
//                if (Rect.intersects(swordRect, coinRect)) {
//                    // Collision detected, set the collided barrier and coin
//                    collidedBarrier = barrier
//                    collidedCoin = coin
//                    println("colliding")
//
//                    // Break out of the loop to remove only one coin
//                    break
//                }
//            }
//
//            // Check if a collision has been detected, and remove only one coin
//            if (collidedBarrier != null && collidedCoin != null) {
//                collidedBarrier.coins.remove(collidedCoin) // Remove the coin from the barrier
//                coins.remove(collidedCoin) // Remove the coin from the global list
//
//                // Reset the collidedBarrier and collidedCoin for the next iteration
//                collidedBarrier = null
//                collidedCoin = null
//            }
//        }


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
            if (isGameOver) {
                isPlaying = false
                exitingGame()
                holder.unlockCanvasAndPost(canvas)
                return
            }
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

//            for (coin in coins) {
//                canvas.drawBitmap(coin.coin, coin.x.toFloat(), coin.y.toFloat(), paint)
//            }
//            for (barrier in listOf(barrier1, barrier2, barrier3, barrier4)) {
//                val coinX = barrier!!.x + (barrier.width / 2) - (coin.width / 2)
//                val coinY = barrier.y - coin.height -40 // Place the coin just above the barrier
////                canvas.drawBitmap(coin.coin, coinX.toFloat(), coinY.toFloat(), paint)
//                for (coin in coins) {
//                    canvas.drawBitmap(coin.coin, coinX.toFloat(), coinY.toFloat(), paint)
//                }
//            }
//            if (coins.isEmpty()) {
//                coinAddHandler.postDelayed({
//                    coins.add(coin1!!)
//                    coins.add(coin2!!)
//                    coins.add(coin3!!)
//                    coins.add(coin4!!)
//                }, 3000) // Delay for 1000 milliseconds (1 second)
//            }
            for (coin in coins) {
                canvas.drawBitmap(coin.coin, coin.x.toFloat(), coin.y.toFloat(), paint)
            }
// Calculate the X-coordinate to position the image on the top right corner with a 10dp right margin
            val x = (canvas.width - score.scoreBitmap.width - convertDpToPx(10)).toFloat()

// Calculate the Y-coordinate to position the image at the top with a 7dp top margin
            val y = convertDpToPx(6).toFloat()
            canvas.drawBitmap(
                score.scoreBitmap,
                x, // X-coordinate where you want to draw the image
                y, // Y-coordinate where you want to draw the image
                paint
            )
// Create a custom Paint object for the score text
            val scorePaint = Paint()
            scorePaint.typeface = ResourcesCompat.getFont(this.context, R.font.germania_one_regular)
            scorePaint.textSize = 48f * resources.displayMetrics.density
            scorePaint.color = Color.WHITE
            scorePaint.textAlign = Paint.Align.CENTER

// Add a shadow to the text
            scorePaint.setShadowLayer(4f, 0f, 4f, Color.parseColor("#286779"))

            val textX = (x / 0.81).toFloat()
            val textY = (y / 0.30).toFloat()

// Draw the score text using the custom Paint object
            canvas.drawText(
                scoreText.toString(),
                textX,
                textY + (score.scoreBitmap.height / 2),
                scorePaint
            )

            //            canvas.drawBitmap(
//                coin1!!.coin,
//                coin1!!.x.toFloat(),
//                coin1!!.y.toFloat(),
//                paint
//            )
//            canvas.drawBitmap(
//                coin2!!.coin,
//                coin2!!.x.toFloat(),
//                coin2!!.y.toFloat(),
//                paint
//            )
//            canvas.drawBitmap(
//               coin3!!.coin,
//               coin3!!.x.toFloat(),
//               coin3!!.y.toFloat(),
//                paint
//            )
//            canvas.drawBitmap(
//                coin4!!.coin,
//                coin4!!.x.toFloat(),
//                coin4!!.y.toFloat(),
//                paint
//            )
            canvas.drawBitmap(sword.sword, sword.x.toFloat(), sword.y.toFloat(), paint)
            // Draw the coin for each barrier


//            for (bullet in bullets) {
//                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint)
//            }
            holder.unlockCanvasAndPost(canvas)
        }

    }

    private fun convertDpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

    fun setGameOverListener(listener: GameOverListener) {
        gameOverListener = listener
    }

    private fun sleep() {
        try {
            Thread.sleep(17)
        } catch (e: Error) {
            println(e)
        }
    }
    private fun exitingGame(){
        try {
            gameOverListener?.onGameOver(scoreText)
            // Check if vibration is enabled in SharedPreferences
            val vibrationEnabled = isVibrationEnabled()

            if (vibrationEnabled) {
                // Vibrate for 500 milliseconds if vibration is enabled
                vibrator?.vibrate(500)
            }
        } catch (e: InterruptedException){
            e.printStackTrace()
        }
    }
    private fun isVibrationEnabled(): Boolean {
        val sharedPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("vibration_enabled", true) // Default to true if the preference doesn't exist
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