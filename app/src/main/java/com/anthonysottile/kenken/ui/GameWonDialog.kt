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

    private fun makeSpacerView(): View {
        val spacerView = View(this.context)
        spacerView.setBackgroundColor(Color.LTGRAY)
        spacerView.layoutParams = GameWonDialog.spacerViewLayoutParams
        return spacerView
    }

    private fun setValues(gameSize: Int, hardMode: Boolean) {
        val statistics = StatisticsManager.getGameStatistics(gameSize, hardMode)

        this.gamesPlayed.text = Integer.toString(statistics.gamesPlayed, 10)
        this.gamesWon.text = Integer.toString(statistics.gamesWon, 10)

        this.averageTime.text = GameWonDialog.toTimeString(
                statistics.totalSeconds / statistics.gamesWon
        )

        this.bestTime.text = GameWonDialog.toTimeString(
                statistics.bestTime
        )

        this.bestTimeDate.text = GameWonDialog.dateFormat.format(statistics.bestTimeDate)
    }

    fun setup(gameSize: Int, hardMode: Boolean, newHighScore: Boolean, ticks: Long) {
        this.setValues(gameSize, hardMode)

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

        val context = this.context

        this.setTitle(context.getString(R.string.youWin))

        val rootLayout = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        )
        val root = LinearLayout(context)
        root.layoutParams = rootLayout
        root.orientation = LinearLayout.VERTICAL

        // Add a spacer at the top
        root.addView(this.makeSpacerView())

        this.newHighScore = TextView(context)
        this.newHighScore.setTextColor(Color.WHITE)
        this.newHighScore.setPadding(
                UIConstants.StatisticsOneIndent * 2,
                5,
                UIConstants.StatisticsOneIndent * 2,
                0
        )

        root.addView(this.newHighScore, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val winTimeLabel = TextView(context)
        winTimeLabel.text = context.getString(R.string.gameTime)
        winTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.winTime = TextView(context)
        this.winTime.setPadding(5, 5, 5, 5)

        val winTimeLayout = LinearLayout(context)
        winTimeLayout.addView(winTimeLabel, GameWonDialog.wrapContent)
        winTimeLayout.addView(View(context), GameWonDialog.middleSpacer)
        winTimeLayout.addView(this.winTime, GameWonDialog.wrapContent)
        winTimeLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(winTimeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        root.addView(this.makeSpacerView())

        val gameTypeLabel = TextView(context)
        gameTypeLabel.text = context.getString(R.string.gameTypeColon)
        gameTypeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gameType = TextView(context)
        this.gameType.setPadding(5, 5, 5, 5)

        val gameTypeLayout = LinearLayout(context)
        gameTypeLayout.addView(gameTypeLabel, GameWonDialog.wrapContent)
        gameTypeLayout.addView(View(context), GameWonDialog.middleSpacer)
        gameTypeLayout.addView(this.gameType, GameWonDialog.wrapContent)
        gameTypeLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(gameTypeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val gameSizeLabel = TextView(context)
        gameSizeLabel.text = context.getString(R.string.gameSizeColon)
        gameSizeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gameSize = TextView(context)
        this.gameSize.setPadding(5, 5, 5, 5)

        val gameSizeLayout = LinearLayout(context)
        gameSizeLayout.addView(gameSizeLabel, GameWonDialog.wrapContent)
        gameSizeLayout.addView(View(context), GameWonDialog.middleSpacer)
        gameSizeLayout.addView(this.gameSize, GameWonDialog.wrapContent)
        gameSizeLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(gameSizeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val gamesPlayedLabel = TextView(context)
        gamesPlayedLabel.text = context.getString(R.string.gamesPlayed)
        gamesPlayedLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gamesPlayed = TextView(context)
        this.gamesPlayed.setPadding(5, 5, 5, 5)

        val gamesPlayedLayout = LinearLayout(context)
        gamesPlayedLayout.addView(gamesPlayedLabel, GameWonDialog.wrapContent)
        gamesPlayedLayout.addView(View(context), GameWonDialog.middleSpacer)
        gamesPlayedLayout.addView(this.gamesPlayed, GameWonDialog.wrapContent)
        gamesPlayedLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(gamesPlayedLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val gamesWonLabel = TextView(context)
        gamesWonLabel.text = context.getString(R.string.gamesWon)
        gamesWonLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gamesWon = TextView(context)
        this.gamesWon.setPadding(5, 5, 5, 5)

        val gamesWonLayout = LinearLayout(context)
        gamesWonLayout.addView(gamesWonLabel, GameWonDialog.wrapContent)
        gamesWonLayout.addView(View(context), GameWonDialog.middleSpacer)
        gamesWonLayout.addView(this.gamesWon, GameWonDialog.wrapContent)
        gamesWonLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(gamesWonLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val averageTimeLabel = TextView(context)
        averageTimeLabel.text = context.getString(R.string.averageTime)
        averageTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.averageTime = TextView(context)
        this.averageTime.setPadding(5, 5, 5, 5)

        val averageTimeLayout = LinearLayout(context)
        averageTimeLayout.addView(averageTimeLabel, GameWonDialog.wrapContent)
        averageTimeLayout.addView(View(context), GameWonDialog.middleSpacer)
        averageTimeLayout.addView(this.averageTime, GameWonDialog.wrapContent)
        averageTimeLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(averageTimeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val bestTimeLabel = TextView(context)
        bestTimeLabel.text = context.getString(R.string.bestTime)
        bestTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.bestTime = TextView(context)
        this.bestTime.setPadding(5, 5, 5, 5)

        val bestTimeLayout = LinearLayout(context)
        bestTimeLayout.addView(bestTimeLabel, GameWonDialog.wrapContent)
        bestTimeLayout.addView(View(context), GameWonDialog.middleSpacer)
        bestTimeLayout.addView(this.bestTime, GameWonDialog.wrapContent)
        bestTimeLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(bestTimeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val bestTimeDateLabel = TextView(context)
        bestTimeDateLabel.text = context.getString(R.string.bestTimeDate)
        bestTimeDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.bestTimeDate = TextView(context)
        this.bestTimeDate.setPadding(5, 5, 5, 5)

        val bestTimeDateLayout = LinearLayout(context)
        bestTimeDateLayout.addView(bestTimeDateLabel, GameWonDialog.wrapContent)
        bestTimeDateLayout.addView(View(context), GameWonDialog.middleSpacer)
        bestTimeDateLayout.addView(this.bestTimeDate, GameWonDialog.wrapContent)
        bestTimeDateLayout.addView(View(context), GameWonDialog.rightPad)
        root.addView(bestTimeDateLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        root.addView(this.makeSpacerView())

        // OK button
        // On click of the OK button the dialog exits
        val okButton = Button(context)
        okButton.text = context.getString(R.string.ok)
        okButton.setOnClickListener { _ -> this@GameWonDialog.dismiss() }

        val buttonsLayout = LinearLayout(context)
        buttonsLayout.addView(
                View(context),
                LinearLayout.LayoutParams(1, 1, .5f)
        )
        buttonsLayout.addView(
                okButton,
                LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        .3f
                )
        )
        buttonsLayout.addView(
                View(context),
                LinearLayout.LayoutParams(1, 1, .5f)
        )

        // Add buttons
        val buttonsLayoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )
        buttonsLayoutParams.setMargins(5, 25, 5, 5)
        root.addView(buttonsLayout, buttonsLayoutParams)

        this.setContentView(root)
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        private fun toTimeString(time: Int): String {
            val seconds = time % 60
            val minutes = time / 60 % 60
            val hours = time / 3600

            return String.format("%02d", hours) +
                    ':'.toString() +
                    String.format("%02d", minutes) +
                    ':'.toString() +
                    String.format("%02d", seconds)
        }

        private val middleSpacer = LinearLayout.LayoutParams(1, 1, 0.1f)

        private val rightPad = LinearLayout.LayoutParams(UIConstants.StatisticsOneIndent * 2, 1)

        private val wrapContent = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )

        private val spacerViewLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1)
    }
}
