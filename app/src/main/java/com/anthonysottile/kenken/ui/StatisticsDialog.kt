package com.anthonysottile.kenken.ui

import android.app.AlertDialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider
import com.anthonysottile.kenken.settings.StatisticsManager
import java.text.SimpleDateFormat
import java.util.*

internal class StatisticsDialog : DialogFragment() {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        this.dialog.setTitle(R.string.statistics)

        val view = inflater.inflate(R.layout.statistics_dialog, container)

        this.dropdown = view.findViewById(R.id.gameSizesSpinner) as Spinner
        this.dropdown.setSelection(SettingsProvider.gameSize - UIConstants.MinGameSize)
        this.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a0: AdapterView<*>, a1: View, a2: Int, a3: Long) {
                this@StatisticsDialog.setValues()
            }

            override fun onNothingSelected(a0: AdapterView<*>) {}
        }
        this.hardModeCheckBox = view.findViewById(R.id.hardModeCheckbox) as CheckBox
        this.hardModeCheckBox.isChecked = SettingsProvider.hardMode
        this.hardModeCheckBox.setOnCheckedChangeListener { _, _ -> this.setValues() }
        this.gamesPlayed = view.findViewById(R.id.gamesPlayed) as TextView
        this.gamesWon = view.findViewById(R.id.gamesWon) as TextView
        this.averageTime = view.findViewById(R.id.averageTime) as TextView
        this.bestTime = view.findViewById(R.id.bestTime) as TextView
        this.bestTimeDate = view.findViewById(R.id.bestTimeDate) as TextView

        view.findViewById(R.id.okButton).setOnClickListener { _ -> this.dismiss() }
        view.findViewById(R.id.clearButton).setOnClickListener { _ ->
            AlertDialog.Builder(view.context)
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

        this.setValues()

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
