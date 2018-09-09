package com.anthonysottile.kenken.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import com.anthonysottile.kenken.R

internal object BitmapCache {

    private lateinit var resources: Resources

    val enabledLeft: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.left)
    }
    val enabledCenter: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.middle)
    }
    val enabledRight: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.right)
    }

    val disabledLeft: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.left_disabled)
    }
    val disabledCenter: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.middle_disabled)
    }
    val disabledRight: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.right_disabled)
    }

    val selectedLeft: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.left_selected)
    }
    val selectedCenter: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.middle_selected)
    }
    val selectedRight: Bitmap by lazy {
        BitmapFactory.decodeResource(this.resources, R.drawable.right_selected)
    }

    fun initialize(resources: Resources) {
        BitmapCache.resources = resources
    }
}
