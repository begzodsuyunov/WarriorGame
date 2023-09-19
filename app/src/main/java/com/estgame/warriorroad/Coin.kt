package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Coin(private val xPosition: Int, screenY: Int, res: Resources) {

    var x = xPosition
    var y = 0
    var coin: Bitmap
    var width = 0
    var height = 0


    init {
        coin = BitmapFactory.decodeResource(res, R.drawable.coin)

        width = coin.width
        height = coin.height

        GameView.screenRatioX?.let { ratioX ->
            GameView.screenRatioY?.let { ratioY ->
                width = (width * ratioX / (2)).toInt()
                height = (height * ratioY * (2)).toInt()
            }
        }
        coin = Bitmap.createScaledBitmap(coin, width, height, false)
        y = screenY - height // Set the y position to the bottom of the screen
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)

    }

}