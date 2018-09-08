package com.anthonysottile.kenken.ui

import android.graphics.Color
import android.graphics.Paint

object UIConstants {

    const val MinGameSize = 4
    const val MaxGameSize = 9
    const val GameSizes = UIConstants.MaxGameSize - UIConstants.MinGameSize + 1

    const val BorderWidth = 2

    const val StatisticsOneIndent = 20

    private var gridColor: Paint? = null

    private var cageColor: Paint? = null

    fun getGridColor(): Paint {
        if (UIConstants.gridColor == null) {
            UIConstants.gridColor = Paint()
            UIConstants.gridColor!!.color = Color.rgb(0xCC, 0xCC, 0xCC)
        }
        return UIConstants.gridColor!!
    }

    fun getCageColor(): Paint {
        if (UIConstants.cageColor == null) {
            UIConstants.cageColor = Paint()
            UIConstants.cageColor!!.color = Color.rgb(0x00, 0x00, 0x00)
        }
        return UIConstants.cageColor!!
    }
}
