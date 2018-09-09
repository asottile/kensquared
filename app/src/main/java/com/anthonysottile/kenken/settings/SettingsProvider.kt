package com.anthonysottile.kenken.settings

import android.content.SharedPreferences
import com.anthonysottile.kenken.ui.UIConstants
import java.util.*

object SettingsProvider {
    private const val GameSize = "GameSize"

    private lateinit var preferences: SharedPreferences

    private var gameSize = UIConstants.MinGameSize

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
     * Returns the setting for the game size.
     *
     * @return The setting for the game size.
     */
    fun getGameSize(): Int {
        return SettingsProvider.gameSize
    }

    /**
     * Sets the game size setting.  Triggers game size changed if it changes.
     *
     * @param gameSize The size of the game to set.
     */
    fun setGameSize(gameSize: Int) {
        if (gameSize != SettingsProvider.gameSize) {
            SettingsProvider.gameSize = gameSize
            val editor = SettingsProvider.preferences.edit()
            editor.putInt(SettingsProvider.GameSize, SettingsProvider.gameSize)
            editor.apply()

            SettingsProvider.triggerGameSizeChanged()
        }
    }

    /**
     * Initialization method to give the manager a reference to preferences.
     *
     * @param preferences A reference to the application preferences.
     */
    fun initialize(preferences: SharedPreferences) {
        SettingsProvider.preferences = preferences
        SettingsProvider.gameSize = SettingsProvider.preferences.getInt(
                SettingsProvider.GameSize,
                UIConstants.MinGameSize
        )
    }
}
