package com.anthonysottile.kenken.ui

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider
import com.anthonysottile.kenken.settings.StatisticsManager
import java.text.SimpleDateFormat
import java.util.*

internal class GameWonDialog : DialogFragment() {
    private var highScore = false
    private var ticks: Long = 0

    fun setup(highScore: Boolean, ticks: Long): GameWonDialog {
        this.highScore = highScore
        this.ticks = ticks
        return this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        this.dialog.setTitle(R.string.youWin)

        val view = inflater.inflate(R.layout.game_won_dialog, container)

        if (highScore) {
            (view.findViewById(R.id.newHighScore) as TextView)
                    .text = this.getString(R.string.newHighScore)
        }

        (view.findViewById(R.id.winTime) as TextView)
                .text = GameWonDialog.toTimeString(ticks.toInt() / 1000)


        val gameType = (view.findViewById(R.id.gameType) as TextView)
        if (SettingsProvider.hardMode) {
            gameType.text = this.getString(R.string.hard)
        } else {
            gameType.text = this.getString(R.string.normal)
        }

        (view.findViewById(R.id.gameSize) as TextView)
                .text = Integer.toString(SettingsProvider.gameSize, 10)

        val statistics = StatisticsManager.getGameStatistics(
                SettingsProvider.gameSize, SettingsProvider.hardMode
        )

        (view.findViewById(R.id.gamesPlayed) as TextView)
                .text = Integer.toString(statistics.gamesPlayed, 10)
        (view.findViewById(R.id.gamesWon) as TextView)
                .text = Integer.toString(statistics.gamesWon, 10)
        (view.findViewById(R.id.averageTime) as TextView)
                .text = GameWonDialog.toTimeString(statistics.totalSeconds / statistics.gamesWon)
        (view.findViewById(R.id.bestTime) as TextView)
                .text = GameWonDialog.toTimeString(statistics.bestTime)
        (view.findViewById(R.id.bestTimeDate) as TextView)
                .text = GameWonDialog.dateFormat.format(statistics.bestTimeDate)

        view.findViewById(R.id.okButton).setOnClickListener { _ -> this@GameWonDialog.dismiss() }

        return view
    }
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        private fun toTimeString(time: Int): String {
            val seconds = time % 60
            val minutes = time / 60 % 60
            val hours = time / 3600

            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}
