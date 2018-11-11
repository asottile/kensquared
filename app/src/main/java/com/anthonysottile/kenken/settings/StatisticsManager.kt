package com.anthonysottile.kenken.settings

import android.content.SharedPreferences

import com.anthonysottile.kenken.ui.UIConstants

import org.json.JSONArray
import org.json.JSONException

object StatisticsManager {
    private const val Statistics = "Statistics"
    private const val HardStatistics = "HardStatistics"
    // TODO: implement dirty cache methodology

    private lateinit var preferences: SharedPreferences
    private lateinit var statistics: Array<GameStatistics>
    private lateinit var hardStatistics: Array<GameStatistics>

    private fun gameSizeToIndex(gameSize: Int): Int {
        return gameSize - UIConstants.MinGameSize
    }

    private fun saveGameStatistics() {
        // Create JSON array
        val statsArray = JSONArray()
        this.statistics.forEach { statsArray.put(it.toJson()) }
        val hardStatsArray = JSONArray()
        this.hardStatistics.forEach { hardStatsArray.put(it.toJson()) }

        val editor = this.preferences.edit()
        editor.putString(this.Statistics, statsArray.toString())
        editor.putString(this.HardStatistics, hardStatsArray.toString())
        editor.apply()
    }

    private fun loadStatistics() {
        if (this.preferences.contains(this.Statistics)) {
            try {
                val arr = JSONArray(
                        this.preferences.getString(StatisticsManager.Statistics, "")
                )
                this.statistics = Array(UIConstants.GameSizes) { i ->
                    GameStatistics(arr.getJSONObject(i))
                }
            } catch (e: JSONException) {
                this.clearStatistics()
            }
        } else {
            this.clearStatistics()

        }
        if (this.preferences.contains(this.HardStatistics)) {
            try {
                val arr = JSONArray(
                        this.preferences.getString(StatisticsManager.HardStatistics, "")
                )
                this.hardStatistics = Array(UIConstants.GameSizes) { i ->
                    GameStatistics(arr.getJSONObject(i))
                }
            } catch (e: JSONException) {
                this.clearHardStatistics()
            }
        } else {
            this.clearHardStatistics()
        }
    }

    private fun clearStatistics() {
        this.statistics = Array(UIConstants.GameSizes) { i ->
            GameStatistics(i + UIConstants.MinGameSize)
        }
    }

    private fun clearHardStatistics() {
        this.hardStatistics = Array(UIConstants.GameSizes) { i ->
            GameStatistics(i + UIConstants.MinGameSize)
        }
    }

    fun clearAllStatistics() {
        this.clearStatistics()
        this.clearHardStatistics()
        this.saveGameStatistics()
    }

    fun getGameStatistics(gameSize: Int, hardMode: Boolean): GameStatistics {
        val index = this.gameSizeToIndex(gameSize)
        val arr = if (hardMode) this.hardStatistics else this.statistics
        return arr[index]
    }

    fun gameStarted(gameSize: Int, hardMode: Boolean) {
        this.getGameStatistics(gameSize, hardMode).gameStarted()
        this.saveGameStatistics()
    }

    /**
     * Call when a game has ended to update statistics.
     * Returns true if this is a new high score.
     *
     * @param gameSize The size of the game completed.
     * @param ticks    The number of ms that the game took.
     * @return True if the game is a new high score.
     */
    fun gameEnded(gameSize: Int, hardMode: Boolean, ticks: Long): Boolean {
        val gameSeconds = (ticks / 1000).toInt()
        val highScore = this.getGameStatistics(gameSize, hardMode).gameWon(gameSeconds)

        this.saveGameStatistics()

        return highScore
    }

    fun initialize(preferences: SharedPreferences) {
        this.preferences = preferences
        this.loadStatistics()
    }
}
