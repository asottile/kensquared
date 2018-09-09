package com.anthonysottile.kenken.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import com.anthonysottile.kenken.R

internal class AboutDialog(context: Context) : Dialog(context) {

    private fun makeSpacerView(): View {
        val spacerView = View(this.context)
        spacerView.setBackgroundColor(Color.LTGRAY)
        spacerView.layoutParams = AboutDialog.spacerViewLayoutParams
        return spacerView
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setTitle(context.getString(R.string.about))

        val rootLayout = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        )
        val root = LinearLayout(this.context)
        root.layoutParams = rootLayout
        root.orientation = LinearLayout.VERTICAL

        // Add a spacer at the top
        root.addView(this.makeSpacerView())

        val appTextBuilder = context.getString(R.string.app_name) +
                ' '.toString() +
                context.getString(R.string.byAuthor)

        val appText = TextView(this.context)
        appText.text = appTextBuilder
        appText.setPadding(5, 5, 5, 5)
        root.addView(appText)

        val versionBuilder = context.getString(R.string.versionColon) +
                ' '.toString() +
                context.getString(R.string.version)

        val versionText = TextView(this.context)
        versionText.text = versionBuilder
        versionText.setPadding(5, 5, 5, 5)
        root.addView(versionText)

        root.addView(this.makeSpacerView())

        val aboutText = TextView(this.context)
        val s = SpannableString(context.getText(R.string.aboutText))
        Linkify.addLinks(s, Linkify.WEB_URLS)
        aboutText.text = s
        aboutText.movementMethod = LinkMovementMethod.getInstance()
        aboutText.setPadding(5, 5, 5, 5)

        root.addView(this.makeSpacerView())

        root.addView(aboutText)

        // OK button
        // On click of the OK button the dialog exits
        val okButton = Button(this.context)
        okButton.text = context.getString(R.string.ok)
        okButton.setOnClickListener { _ -> this@AboutDialog.dismiss() }

        val buttonsLayout = LinearLayout(this.context)
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
        private val spacerViewLayoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1)
    }
}
