package com.anthonysottile.kenken.settings

import android.content.SharedPreferences
import com.anthonysottile.kenken.ui.UIConstants
import java.util.*

object SettingsProvider {
    private const val GameSize = "GameSize"

    private lateinit var preferences: SharedPreferences

    var gameSize = UIConstants.MinGameSize
        set (gameSize) {
            if (field != gameSize) {
                field = gameSize

                val editor = this.preferences.edit()
                editor.putInt(this.GameSize, this.gameSize)
                editor.apply()

                SettingsProvider.triggerGameSizeChanged()
            }
        }

    private val gameSizeChangedListeners = ArrayList<() -> Unit>()

    // TODO: implement dirty cache methodology

    fun addGameSizeChangedListener(listener: () -> Unit) {
        SettingsProvider.gameSizeChangedListeners.add(listener)
    }

    private fun triggerGameSizeChanged() {
        for (listener in SettingsProvider.gameSizeChangedListeners) {
            listener()
        }
    }

    /**
     * Initialization method to give the manager a reference to preferences.
     *
     * @param preferences A reference to the application preferences.
     */
    fun initialize(preferences: SharedPreferences) {
        this.preferences = preferences
        this.gameSize = SettingsProvider.preferences.getInt(
                SettingsProvider.GameSize,
                UIConstants.MinGameSize
        )
    }
}
