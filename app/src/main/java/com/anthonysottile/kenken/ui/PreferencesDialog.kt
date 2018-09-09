package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider

internal class PreferencesDialog(context: Context) : Dialog(context) {

    private lateinit var dropdown: Spinner

    private fun makeSpacerView(): View {
        val spacerView = View(this.context)
        spacerView.setBackgroundColor(Color.LTGRAY)
        spacerView.layoutParams = PreferencesDialog.spacerViewLayoutParams
        return spacerView
    }

    /**
     * Sets the Dialog's spinner to the value specified.
     *
     * @param gameSize The gameSize to set.  Note: this should be between
     * [UIConstants.MinGameSize]
     * and [UIConstants.MaxGameSize].
     */
    fun setSpinner(gameSize: Int) {
        this.dropdown.setSelection(gameSize - UIConstants.MinGameSize)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this.context

        this.setTitle(context.getString(R.string.preferences))

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
                PreferencesDialog.GameSizes
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        this.dropdown.adapter = spinnerAdapter
        this.dropdown.setSelection(0)

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

        // Warning text label.  This is warning that the current game ends when
        //  clicking on preferences.
        val warning = TextView(context)
        warning.text = context.getString(R.string.preferencesWarning)
        warning.setPadding(10, 10, 10, 10)

        // OK button
        // On click of the OK button, the value is saved and the dialog exits
        val okButton = Button(context)
        okButton.text = context.getString(R.string.ok)
        okButton.setOnClickListener { _ ->
            SettingsProvider.gameSize =
                    this@PreferencesDialog.dropdown.selectedItemPosition + UIConstants.MinGameSize

            this@PreferencesDialog.dismiss()
        }

        // Cancel button
        // Doesn't set the changed value here.
        val cancelButton = Button(context)
        cancelButton.text = context.getString(R.string.cancel)
        cancelButton.setOnClickListener { _ -> this@PreferencesDialog.cancel() }

        // Layout for the OK and Cancel buttons
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
                cancelButton,
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

        val rootLayout = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )

        // Root layout
        val root = LinearLayout(context)
        root.layoutParams = rootLayout
        root.orientation = LinearLayout.VERTICAL

        // Add spacer
        root.addView(this.makeSpacerView())

        // Add game size
        root.addView(
                gameSizeLayout,
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        )

        // Add warning text
        root.addView(
                warning,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        )

        // Add spacer
        root.addView(this.makeSpacerView())

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

        private val spacerViewLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1)
    }
}
