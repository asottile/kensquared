package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.*
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider

internal class PreferencesDialog : DialogFragment() {
    private lateinit var dropdown: Spinner
    private lateinit var hardModeCheckBox: CheckBox

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        this.dialog.setTitle(R.string.preferences)

        val view = inflater.inflate(R.layout.preferences_dialog, container)

        this.dropdown = view.findViewById(R.id.gameSizesSpinner) as Spinner
        this.dropdown.setSelection(SettingsProvider.gameSize - UIConstants.MinGameSize)
        this.hardModeCheckBox = view.findViewById(R.id.hardModeCheckbox) as CheckBox
        this.hardModeCheckBox.isChecked = SettingsProvider.hardMode

        (view.findViewById(R.id.okButton) as Button).setOnClickListener { _ ->
            SettingsProvider.gameSize = this.dropdown.selectedItemPosition + UIConstants.MinGameSize
            SettingsProvider.hardMode = this.hardModeCheckBox.isChecked

            this.dismiss()
        }
        (view.findViewById(R.id.cancelButton) as Button).setOnClickListener { _ -> this.dismiss() }

        return view
    }
}
