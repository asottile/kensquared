package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.anthonysottile.kenken.R

internal class AboutDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View {
        this.dialog.setTitle(R.string.about)

        val view = inflater.inflate(R.layout.about_dialog, container)
        (view.findViewById(R.id.byText) as TextView).text =
            this.getString(R.string.byAuthor, this.getString(R.string.app_name))
        (view.findViewById(R.id.versionText) as TextView).text =
            this.getString(R.string.versionColon, this.getString(R.string.version))
        view.findViewById(R.id.okButton).setOnClickListener { _ -> this.dismiss() }
        return view
    }
}
