package com.anthonysottile.kenken.settings

import android.content.SharedPreferences
import com.anthonysottile.kenken.ui.UIConstants
import java.util.*

object SettingsProvider {
    private const val GameSize = "GameSize"
    private const val HardMode = "HardMode"

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

    var hardMode = false
        set (hardMode) {
            if (field != hardMode) {
                field = hardMode

                val editor = this.preferences.edit()
                editor.putBoolean(this.HardMode, this.hardMode)
                editor.apply()

                SettingsProvider.triggerHardModeChanged()
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

    private val hardModeChangedListeners = ArrayList<() -> Unit>()

    fun addHardModeChangedListener(listener: () -> Unit) {
        SettingsProvider.hardModeChangedListeners.add(listener)
    }

    private fun triggerHardModeChanged() {
        for (listener in SettingsProvider.hardModeChangedListeners) {
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
        this.hardMode = SettingsProvider.preferences.getBoolean(
                SettingsProvider.HardMode,
                false
        )
    }
}
