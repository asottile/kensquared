package com.anthonysottile.kenken.ui

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Spinner
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider

internal class PreferencesDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        this.dialog.setTitle(R.string.preferences)

        val view = inflater.inflate(R.layout.preferences_dialog, container)

        val dropdown = view.findViewById(R.id.gameSizesSpinner) as Spinner
        dropdown.setSelection(SettingsProvider.gameSize - UIConstants.MinGameSize)
        val hardModeCheckBox = view.findViewById(R.id.hardModeCheckbox) as CheckBox
        hardModeCheckBox.isChecked = SettingsProvider.hardMode

        view.findViewById(R.id.okButton).setOnClickListener { _ ->
            SettingsProvider.gameSize = dropdown.selectedItemPosition + UIConstants.MinGameSize
            SettingsProvider.hardMode = hardModeCheckBox.isChecked

            this.dismiss()
        }
        view.findViewById(R.id.cancelButton).setOnClickListener { _ -> this.dismiss() }

        return view
    }
}
