package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory


class Background(screenX: Int, screenY: Int, res: Resources) {
    var x = 0 // Set the initial x position
    var y = 0
    var background: Bitmap

    init {
        background = BitmapFactory.decodeResource(res, R.drawable.gamescreenback)
        background = Bitmap.createScaledBitmap(background, screenX *2, screenY, false)

    }
}
