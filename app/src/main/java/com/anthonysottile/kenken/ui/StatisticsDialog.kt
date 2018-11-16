package com.anthonysottile.kenken.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.StatisticsManager
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

internal class StatisticsDialog(context: Context) : Dialog(context) {

    private lateinit var dropdown: Spinner
    private lateinit var hardModeCheckBox: CheckBox
    private lateinit var gamesPlayed: TextView
    private lateinit var gamesWon: TextView
    private lateinit var averageTime: TextView
    private lateinit var bestTime: TextView
    private lateinit var bestTimeDate: TextView

    fun setValues() {
        val gameSize = UIConstants.MinGameSize + this.dropdown.selectedItemPosition
        val hardMode = this.hardModeCheckBox.isChecked
        val statistics = StatisticsManager.getGameStatistics(gameSize, hardMode)

        this.gamesPlayed.text = Integer.toString(statistics.gamesPlayed, 10)
        this.gamesWon.text = Integer.toString(statistics.gamesWon, 10)

        if (statistics.gamesWon > 0) {
            this.averageTime.text = StatisticsDialog.toTimeString(
                    statistics.totalSeconds / statistics.gamesWon
            )

            this.bestTime.text = StatisticsDialog.toTimeString(
                    statistics.bestTime
            )

            this.bestTimeDate.text = StatisticsDialog.dateFormat.format(statistics.bestTimeDate)

        } else {
            this.averageTime.text = ""
            this.bestTime.text = ""
            this.bestTimeDate.text = ""
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTitle(R.string.statistics)

        this.setContentView(R.layout.statistics_dialog)

        this.dropdown = this.findViewById(R.id.gameSizesSpinner) as Spinner
        this.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a0: AdapterView<*>, a1: View, a2: Int, a3: Long) {
                this@StatisticsDialog.setValues()
            }

            override fun onNothingSelected(a0: AdapterView<*>) {}
        }
        this.hardModeCheckBox = this.findViewById(R.id.hardModeCheckbox) as CheckBox
        this.hardModeCheckBox.setOnCheckedChangeListener { _, _ -> this.setValues() }
        this.gamesPlayed = this.findViewById(R.id.gamesPlayed) as TextView
        this.gamesWon = this.findViewById(R.id.gamesWon) as TextView
        this.averageTime = this.findViewById(R.id.averageTime) as TextView
        this.bestTime = this.findViewById(R.id.bestTime) as TextView
        this.bestTimeDate = this.findViewById(R.id.bestTimeDate) as TextView

        this.findViewById(R.id.okButton).setOnClickListener { _ -> this.dismiss() }
        this.findViewById(R.id.clearButton).setOnClickListener { _ ->
            AlertDialog.Builder(this.context)
                    .setMessage(R.string.confirmClear)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        StatisticsManager.clearAllStatistics()
                        this.setValues()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
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
    }
}
