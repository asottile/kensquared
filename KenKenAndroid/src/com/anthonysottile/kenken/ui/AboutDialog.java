package com.anthonysottile.kenken.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anthonysottile.kenken.R;

public class AboutDialog extends Dialog {

	public AboutDialog(Context context) {
		super(context);
	}

	private static final LinearLayout.LayoutParams spacerViewLayoutParams =
		new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
	private View getSpacerView() {
		View spacerView = new View(this.getContext());
        spacerView.setBackgroundColor(Color.LTGRAY);
        spacerView.setLayoutParams(AboutDialog.spacerViewLayoutParams);
        return spacerView;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this.getContext();

		this.setTitle(context.getString(R.string.about));

        LinearLayout.LayoutParams rootLayout =
    		new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
			);
        LinearLayout root = new LinearLayout(context);
        root.setLayoutParams(rootLayout);
        root.setOrientation(LinearLayout.VERTICAL);

        // Add a spacer at the top
        root.addView(this.getSpacerView());

        StringBuilder appTextBuilder = new StringBuilder();
        appTextBuilder.append(context.getString(R.string.app_name));
        appTextBuilder.append(' ');
        appTextBuilder.append(context.getString(R.string.byAuthor));

        TextView appText = new TextView(context);
        appText.setText(appTextBuilder.toString());
        appText.setPadding(5, 5, 5, 5);
        root.addView(appText);

        StringBuilder versionBuidler = new StringBuilder();
        versionBuidler.append(context.getString(R.string.versionColon));
        versionBuidler.append(' ');
        versionBuidler.append(context.getString(R.string.version));

        TextView versionText = new TextView(context);
        versionText.setText(versionBuidler.toString());
        versionText.setPadding(5, 5, 5, 5);
        root.addView(versionText);

        root.addView(this.getSpacerView());

        TextView aboutText = new TextView(context);
        final SpannableString s =
             new SpannableString(context.getText(R.string.aboutText));
        Linkify.addLinks(s, Linkify.WEB_URLS);
        aboutText.setText(s);
        aboutText.setMovementMethod(LinkMovementMethod.getInstance());
        aboutText.setPadding(5, 5, 5, 5);

        root.addView(this.getSpacerView());

        root.addView(aboutText);

        // OK button
        // On click of the OK button the dialog exits
        Button okButton = new Button(context);
        okButton.setText(context.getString(R.string.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				AboutDialog.this.dismiss();
			}
        });

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.addView(
    		new View(context),
    		new LinearLayout.LayoutParams(1, 1, .5f)
		);
        buttonsLayout.addView(
    		okButton,
    		new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT,
				.3f
			)
		);
        buttonsLayout.addView(
    		new View(context),
    		new LinearLayout.LayoutParams(1, 1, .5f)
		);

        // Add buttons
        LinearLayout.LayoutParams buttonsLayoutParams =
        	new LinearLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.WRAP_CONTENT
			);
        buttonsLayoutParams.setMargins(5, 25, 5, 5);
        root.addView(buttonsLayout, buttonsLayoutParams);

        this.setContentView(root);
	}

}
