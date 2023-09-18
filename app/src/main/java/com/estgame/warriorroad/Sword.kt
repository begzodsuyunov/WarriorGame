package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.estgame.warriorroad.GameView.Companion.screenRatioX
import com.estgame.warriorroad.GameView.Companion.screenRatioY

class Sword (private val screenY: Int, res: Resources) {
    var isForward: Boolean = false
    var isJumping: Boolean = false
    private var velocity: Int = 0
    var x = 0
    var y = 0
    var sword: Bitmap
    var width = 0
    var height = 0


    init {
        sword = BitmapFactory.decodeResource(res, R.drawable.sword)

        width = sword.width
        height = sword.height

        screenRatioX?.let { ratioX ->
            screenRatioY?.let { ratioY ->
                width = (width * ratioX / (2)).toInt()
                height = (height * ratioY * (2)).toInt()
            }
        }
        sword = Bitmap.createScaledBitmap(sword, width, height, false)

        x = (2 * screenRatioX!!).toInt()
        y = screenY - height - (1000 * screenRatioY!!).toInt() // Adjust the value (50) as needed

    }
    fun jump() {
        if (!isJumping) {
            isJumping = true
            velocity = -50  // Apply an upward velocity when jumping
        }
    }

    fun update() {
        if (isJumping) {
            // Apply the velocity to move the sword upwards
            y += velocity
            // Apply gravity to bring the sword back down
            velocity += 2  // You can adjust the gravity strength as needed
            // Check if the sword has landed
            if (y >= screenY - height - (1000 * screenRatioY!!).toInt()) {
                y = screenY - height - (1000 * screenRatioY!!).toInt() // Adjust the value (50) as needed
                isJumping = false
            }
        }
    }
    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

}