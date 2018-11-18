package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.StatisticsManager
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

internal class GameWonDialog(context: Context) : Dialog(context) {
    private lateinit var newHighScore: TextView
    private lateinit var winTime: TextView

    private lateinit var gameSize: TextView
    private lateinit var gameType: TextView
    private lateinit var gamesPlayed: TextView
    private lateinit var gamesWon: TextView
    private lateinit var averageTime: TextView
    private lateinit var bestTime: TextView
    private lateinit var bestTimeDate: TextView

    fun setup(gameSize: Int, hardMode: Boolean, newHighScore: Boolean, ticks: Long) {
        val statistics = StatisticsManager.getGameStatistics(gameSize, hardMode)

        this.gamesPlayed.text = Integer.toString(statistics.gamesPlayed, 10)
        this.gamesWon.text = Integer.toString(statistics.gamesWon, 10)

        this.averageTime.text = GameWonDialog.toTimeString(
                statistics.totalSeconds / statistics.gamesWon
        )

        this.bestTime.text = GameWonDialog.toTimeString(statistics.bestTime)

        this.bestTimeDate.text = GameWonDialog.dateFormat.format(statistics.bestTimeDate)

        if (hardMode) {
            this.gameType.text = this.context.getString(R.string.hard)
        } else {
            this.gameType.text = this.context.getString(R.string.normal)
        }

        this.gameSize.text = Integer.toString(gameSize, 10)
        this.winTime.text = GameWonDialog.toTimeString(ticks.toInt() / 1000)

        if (newHighScore) {
            this.newHighScore.text = this.context.getString(R.string.newHighScore)
        } else {
            this.newHighScore.text = ""
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTitle(context.getString(R.string.youWin))

        this.setContentView(R.layout.game_won_dialog)
        this.newHighScore = this.findViewById(R.id.newHighScore) as TextView
        this.winTime = this.findViewById(R.id.winTime) as TextView

        this.gameType = this.findViewById(R.id.gameType) as TextView
        this.gameSize = this.findViewById(R.id.gameSize) as TextView
        this.gamesPlayed = this.findViewById(R.id.gamesPlayed) as TextView
        this.gamesWon = this.findViewById(R.id.gamesWon) as TextView
        this.averageTime = this.findViewById(R.id.averageTime) as TextView
        this.bestTime = this.findViewById(R.id.bestTime) as TextView
        this.bestTimeDate = this.findViewById(R.id.bestTimeDate) as TextView

        this.findViewById(R.id.okButton).setOnClickListener { _ -> this@GameWonDialog.dismiss() }
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
