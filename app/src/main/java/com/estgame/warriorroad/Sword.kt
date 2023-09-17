package com.estgame.warriorroad

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.estgame.warriorroad.GameView.Companion.screenRatioX
import com.estgame.warriorroad.GameView.Companion.screenRatioY

class Sword (screenY: Int, res: Resources) {
    var isGoingUp: Boolean = false
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
                width = (width * ratioX / (1.5)).toInt()
                height = (height * ratioY * (2.5)).toInt()
            }
        }
        sword = Bitmap.createScaledBitmap(sword, width, height, false)

        x = (2 * screenRatioX!!).toInt()
        y = screenY - height - (1000 * screenRatioY!!).toInt() // Adjust the value (50) as needed
    }


}