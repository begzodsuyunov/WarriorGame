package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Score(res: Resources) {
    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0
    var scoreBitmap: Bitmap
    val coins = ArrayList<Coin>()

    init {
        scoreBitmap = BitmapFactory.decodeResource(res, R.drawable.score_main)


        width = scoreBitmap.width
        height = scoreBitmap.height

        width = (width / 3.6).toInt()
        height = (height * 1.05).toInt()


        width = (width * GameView.screenRatioX!!).toInt()
        height = (height * GameView.screenRatioY!!).toInt()

        scoreBitmap = Bitmap.createScaledBitmap(scoreBitmap, width, height, false)
    }
}