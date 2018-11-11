package com.anthonysottile.kenken.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.StatisticsManager
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

    private fun makeSpacerView(): View {
        val spacerView = View(this.context)
        spacerView.setBackgroundColor(Color.LTGRAY)
        spacerView.layoutParams = StatisticsDialog.spacerViewLayoutParams
        return spacerView
    }

    // TODO: make this support hardMode
    private fun setValues() {
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

    private fun clearStatistics() {
        StatisticsManager.clearAllStatistics()
        this.setValues()
    }

    /**
     * refresh the display of the Statistics Dialog.  This is so when showing
     * the dialog again after the first show it is updated with whatever
     * games were played.
     */
    fun refresh() {
        this.setValues()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this.context

        this.setTitle(context.getString(R.string.statistics))

        val rootLayout = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        )
        val root = LinearLayout(context)
        root.layoutParams = rootLayout
        root.orientation = LinearLayout.VERTICAL

        // Add a spacer at the top
        root.addView(this.makeSpacerView())

        // Game size Label
        val gameSizeLabel = TextView(context)
        gameSizeLabel.text = context.getString(R.string.gameSize)
        gameSizeLabel.textSize = 18f
        gameSizeLabel.setPadding(10, 10, 10, 10)
        gameSizeLabel.setTextColor(Color.WHITE)

        // Game Size dropdown
        this.dropdown = Spinner(context)
        val spinnerAdapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                StatisticsDialog.GameSizes
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        this.dropdown.adapter = spinnerAdapter
        this.dropdown.setSelection(0)
        this.dropdown.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapter: AdapterView<*>, view: View,
                                        selectedIndex: Int, id: Long) {
                this@StatisticsDialog.setValues()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {}
        }

        // Layout for Game Size
        // NOTE: Needed a little hack here to get the dialog to display nicely.
        //       I'm not entirely sure why I cannot just set weight to .5 on both
        //        and remove the spacer guy.  But this renders approximately how
        //        I wanted it to anyways.
        val gameSizeLayout = LinearLayout(context)
        gameSizeLayout.addView(
                gameSizeLabel,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )
        gameSizeLayout.addView(
                View(context),
                LinearLayout.LayoutParams(1, 1, 0.3f)
        )
        gameSizeLayout.addView(
                this.dropdown,
                LinearLayout.LayoutParams(1, LayoutParams.WRAP_CONTENT, 0.7f)
        )

        root.addView(gameSizeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val hardModeLabel = TextView(context)
        hardModeLabel.text = context.getString(R.string.hardMode)
        hardModeLabel.textSize = 18f
        hardModeLabel.setPadding(10, 10, 10, 10)
        hardModeLabel.setTextColor(Color.WHITE)

        this.hardModeCheckBox = CheckBox(context)
        hardModeCheckBox.setOnCheckedChangeListener { _, _ -> this@StatisticsDialog.setValues() }

        val hardModeLayout = LinearLayout(context)
        hardModeLayout.addView(hardModeLabel, StatisticsDialog.wrapContent)
        gameSizeLayout.addView(View(context), StatisticsDialog.middleSpacer)
        // TODO: when switching to xml make sure this right aligns nicely.
        hardModeLayout.addView(this.hardModeCheckBox, StatisticsDialog.wrapContent)

        root.addView(hardModeLayout)

        root.addView(this.makeSpacerView())

        val gamesPlayedLabel = TextView(context)
        gamesPlayedLabel.text = context.getString(R.string.gamesPlayed)
        gamesPlayedLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gamesPlayed = TextView(context)
        this.gamesPlayed.setPadding(5, 5, 5, 5)

        val gamesPlayedLayout = LinearLayout(context)
        gamesPlayedLayout.addView(gamesPlayedLabel, StatisticsDialog.wrapContent)
        gamesPlayedLayout.addView(View(context), StatisticsDialog.middleSpacer)
        gamesPlayedLayout.addView(this.gamesPlayed, StatisticsDialog.wrapContent)
        gamesPlayedLayout.addView(View(context), StatisticsDialog.rightPad)
        root.addView(gamesPlayedLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val gamesWonLabel = TextView(context)
        gamesWonLabel.text = context.getString(R.string.gamesWon)
        gamesWonLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.gamesWon = TextView(context)
        this.gamesWon.setPadding(5, 5, 5, 5)

        val gamesWonLayout = LinearLayout(context)
        gamesWonLayout.addView(gamesWonLabel, StatisticsDialog.wrapContent)
        gamesWonLayout.addView(View(context), StatisticsDialog.middleSpacer)
        gamesWonLayout.addView(this.gamesWon, StatisticsDialog.wrapContent)
        gamesWonLayout.addView(View(context), StatisticsDialog.rightPad)
        root.addView(gamesWonLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val averageTimeLabel = TextView(context)
        averageTimeLabel.text = context.getString(R.string.averageTime)
        averageTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.averageTime = TextView(context)
        this.averageTime.setPadding(5, 5, 5, 5)

        val averageTimeLayout = LinearLayout(context)
        averageTimeLayout.addView(averageTimeLabel, StatisticsDialog.wrapContent)
        averageTimeLayout.addView(View(context), StatisticsDialog.middleSpacer)
        averageTimeLayout.addView(this.averageTime, StatisticsDialog.wrapContent)
        averageTimeLayout.addView(View(context), StatisticsDialog.rightPad)
        root.addView(averageTimeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val bestTimeLabel = TextView(context)
        bestTimeLabel.text = context.getString(R.string.bestTime)
        bestTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.bestTime = TextView(context)
        this.bestTime.setPadding(5, 5, 5, 5)

        val bestTimeLayout = LinearLayout(context)
        bestTimeLayout.addView(bestTimeLabel, StatisticsDialog.wrapContent)
        bestTimeLayout.addView(View(context), StatisticsDialog.middleSpacer)
        bestTimeLayout.addView(this.bestTime, StatisticsDialog.wrapContent)
        bestTimeLayout.addView(View(context), StatisticsDialog.rightPad)
        root.addView(bestTimeLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val bestTimeDateLabel = TextView(context)
        bestTimeDateLabel.text = context.getString(R.string.bestTimeDate)
        bestTimeDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5)
        this.bestTimeDate = TextView(context)
        this.bestTimeDate.setPadding(5, 5, 5, 5)

        val bestTimeDateLayout = LinearLayout(context)
        bestTimeDateLayout.addView(bestTimeDateLabel, StatisticsDialog.wrapContent)
        bestTimeDateLayout.addView(View(context), StatisticsDialog.middleSpacer)
        bestTimeDateLayout.addView(this.bestTimeDate, StatisticsDialog.wrapContent)
        bestTimeDateLayout.addView(View(context), StatisticsDialog.rightPad)
        root.addView(bestTimeDateLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        root.addView(this.makeSpacerView())

        // OK button
        // On click of the OK button the dialog exits
        val okButton = Button(context)
        okButton.text = context.getString(R.string.ok)
        okButton.setOnClickListener { _ -> this@StatisticsDialog.dismiss() }

        // Cancel button
        // Doesn't set the changed value here.
        val clearButton = Button(context)
        clearButton.setText(R.string.clear)
        clearButton.setOnClickListener { _ ->
            val builder = AlertDialog.Builder(this@StatisticsDialog.context)
            builder.setMessage(R.string.confirmClear)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes) { dialog, _ ->
                        this@StatisticsDialog.clearStatistics()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            val alert = builder.create()
            alert.show()
        }

        // Layout for the OK and Clear buttons
        // NOTE: I had to do some weird things to prevent the dialog displaying
        //        weirdly.  Still not sure why this works...
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
                clearButton,
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
        private val GameSizes = arrayOf("4", "5", "6", "7", "8", "9")

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
