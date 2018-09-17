package com.anthonysottile.kenken.settings

import android.content.SharedPreferences

import com.anthonysottile.kenken.ui.UIConstants

import org.json.JSONArray
import org.json.JSONException

object StatisticsManager {
    private const val Statistics = "Statistics"
    // TODO: implement dirty cache methodology

    private lateinit var preferences: SharedPreferences
    private lateinit var statistics: Array<GameStatistics>

    private fun gameSizeToIndex(gameSize: Int): Int {
        return gameSize - UIConstants.MinGameSize
    }

    private fun saveGameStatistics() {
        // Create JSON array
        val array = JSONArray()
        this.statistics.forEach { array.put(it.toJson()) }

        val editor = this.preferences.edit()
        editor.putString(this.Statistics, array.toString())
        editor.apply()
    }

    private fun loadStatistics() {
        if (!this.preferences.contains(this.Statistics)) {
            this.clearGameStatistics()
        } else {
            try {
                val array = JSONArray(
                        this.preferences.getString(
                                StatisticsManager.Statistics, ""
                        )
                )

                this.statistics = Array(UIConstants.GameSizes) { i ->
                    GameStatistics(array.getJSONObject(i))
                }

            } catch (e: JSONException) {
                this.clearGameStatistics()
            }
        }
    }

    fun clearGameStatistics() {
        this.statistics = Array(UIConstants.GameSizes) { i ->
            GameStatistics(i + UIConstants.MinGameSize)
        }
        this.saveGameStatistics()
    }

    fun getGameStatistics(gameSize: Int): GameStatistics {
        return this.statistics[this.gameSizeToIndex(gameSize)]
    }

    fun gameStarted(gameSize: Int) {
        this.statistics[this.gameSizeToIndex(gameSize)].gameStarted()
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
    fun gameEnded(gameSize: Int, ticks: Long): Boolean {
        val game = StatisticsManager.statistics[this.gameSizeToIndex(gameSize)]
        val gameSeconds = (ticks / 1000).toInt()

        val highScore = game.gameWon(gameSeconds)

        this.saveGameStatistics()

        return highScore
    }

    fun initialize(preferences: SharedPreferences) {
        this.preferences = preferences
        this.loadStatistics()
    }
}
