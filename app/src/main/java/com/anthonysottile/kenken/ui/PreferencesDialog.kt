package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider

internal class PreferencesDialog(context: Context) : Dialog(context) {

    private lateinit var dropdown: Spinner
    private lateinit var hardModeCheckBox: CheckBox

    fun setSpinner(gameSize: Int) {
        this.dropdown.setSelection(gameSize - UIConstants.MinGameSize)
    }

    fun setHardMode(hard: Boolean) {
        this.hardModeCheckBox.isChecked = hard
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTitle(this.context.getString(R.string.preferences))

        this.setContentView(R.layout.preferences_dialog)

        this.dropdown = this.findViewById(R.id.gameSizesSpinner) as Spinner
        this.hardModeCheckBox = this.findViewById(R.id.hardModeCheckbox) as CheckBox

        (this.findViewById(R.id.okButton) as Button).setOnClickListener { _ ->
            SettingsProvider.gameSize = this.dropdown.selectedItemPosition + UIConstants.MinGameSize
            SettingsProvider.hardMode = this.hardModeCheckBox.isChecked

            this.dismiss()
        }
        (this.findViewById(R.id.cancelButton) as Button).setOnClickListener { _ -> this.dismiss() }
    }
}
