package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Coin(res: Resources) {

    var x = 0
    var y = 0
    var coin: Bitmap
    internal var width = 0
    internal var height = 0


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

    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

}