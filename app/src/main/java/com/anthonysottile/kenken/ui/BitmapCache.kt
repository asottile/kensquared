package com.anthonysottile.kenken.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import com.anthonysottile.kenken.R

internal object BitmapCache {

    private var resources: Resources? = null

    private var enabledLeft: Bitmap? = null
    private var enabledCenter: Bitmap? = null
    private var enabledRight: Bitmap? = null

    private var disabledLeft: Bitmap? = null
    private var disabledCenter: Bitmap? = null
    private var disabledRight: Bitmap? = null

    private var selectedLeft: Bitmap? = null
    private var selectedCenter: Bitmap? = null
    private var selectedRight: Bitmap? = null

    fun getEnabledLeft(): Bitmap {
        if (BitmapCache.enabledLeft == null) {
            BitmapCache.enabledLeft = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left)
        }
        return BitmapCache.enabledLeft!!
    }

    fun getEnabledCenter(): Bitmap {
        if (BitmapCache.enabledCenter == null) {
            BitmapCache.enabledCenter = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle)
        }
        return BitmapCache.enabledCenter!!
    }

    fun getEnabledRight(): Bitmap {
        if (BitmapCache.enabledRight == null) {
            BitmapCache.enabledRight = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right)
        }
        return BitmapCache.enabledRight!!
    }

    fun getDisabledLeft(): Bitmap {
        if (BitmapCache.disabledLeft == null) {
            BitmapCache.disabledLeft = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left_disabled)
        }
        return BitmapCache.disabledLeft!!
    }

    fun getDisabledCenter(): Bitmap {
        if (BitmapCache.disabledCenter == null) {
            BitmapCache.disabledCenter = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle_disabled)
        }
        return BitmapCache.disabledCenter!!
    }

    fun getDisabledRight(): Bitmap {
        if (BitmapCache.disabledRight == null) {
            BitmapCache.disabledRight = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right_disabled)
        }
        return BitmapCache.disabledRight!!
    }

    fun getSelectedLeft(): Bitmap {
        if (BitmapCache.selectedLeft == null) {
            BitmapCache.selectedLeft = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.left_selected)
        }
        return BitmapCache.selectedLeft!!
    }

    fun getSelectedCenter(): Bitmap {
        if (BitmapCache.selectedCenter == null) {
            BitmapCache.selectedCenter = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.middle_selected)
        }
        return BitmapCache.selectedCenter!!
    }

    fun getSelectedRight(): Bitmap {
        if (BitmapCache.selectedRight == null) {
            BitmapCache.selectedRight = BitmapFactory.decodeResource(BitmapCache.resources, R.drawable.right_selected)
        }
        return BitmapCache.selectedRight!!
    }

    /**
     * Initialization method to give the cache a reference to resources.
     *
     * @param resources Application resources to load bitmaps from.
     */
    fun initialize(resources: Resources) {
        BitmapCache.resources = resources
    }
}
