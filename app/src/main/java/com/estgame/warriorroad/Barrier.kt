package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.estgame.warriorroad.GameView.Companion.screenRatioX
import com.estgame.warriorroad.GameView.Companion.screenRatioY


class Barrier(private val xPosition: Int, screenY: Int, res: Resources) {
    var x: Int = xPosition
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0
    var barrierBitmap: Bitmap
    val coins = ArrayList<Coin>()

    init {
        barrierBitmap = BitmapFactory.decodeResource(res, R.drawable.barrier)


        width = barrierBitmap.width
        height = barrierBitmap.height

        width /= 3


        width = (width * screenRatioX!!).toInt()
        height = (height * screenRatioY!!).toInt()

        barrierBitmap = Bitmap.createScaledBitmap(barrierBitmap, width, height, false)
        y = screenY - height // Set the y position to the bottom of the screen

    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

}