package com.anthonysottile.kenken.ui;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class PreferencesDialog extends Dialog {

	private static final String[] GameSizes = {
		"4", "5", "6", "7", "8", "9"
	};
	
	private Spinner dropdown = null;

	/**
	 * Sets the Dialog's spinner to the value specified.
	 * 
	 * @param gameSize The gameSize to set.  Note: this should be between
	 *                  {@link UIConstants#MinGameSize}
	 *                  and {@link UIConstants.MaxGameSize}.
	 */
	public void SetSpinner(int gameSize) {
		this.dropdown.setSelection(gameSize - UIConstants.MinGameSize);
	}
	
	public PreferencesDialog(Context context) {
		super(context);
	}

	private static final LinearLayout.LayoutParams spacerViewLayoutParams =
		new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
	private View getSpacerView() {
		View spacerView = new View(this.getContext());
        spacerView.setBackgroundColor(Color.LTGRAY);
        spacerView.setLayoutParams(spacerViewLayoutParams);
        return spacerView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Context context = this.getContext();
        
		this.setTitle(context.getString(R.string.preferences));
		        
		// Game size Label
        TextView gameSizeLabel = new TextView(context);
        gameSizeLabel.setText(context.getString(R.string.gameSize));
        gameSizeLabel.setTextSize(18);
        gameSizeLabel.setPadding(10, 10, 10, 10);
        gameSizeLabel.setTextColor(Color.WHITE);
        
        // Game Size dropdown
        this.dropdown = new Spinner(context);
        ArrayAdapter<String> spinnerAdapter =
    		new ArrayAdapter<String>(
				context,
				android.R.layout.simple_spinner_item,
				PreferencesDialog.GameSizes
			);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.dropdown.setAdapter(spinnerAdapter);
        this.dropdown.setSelection(SettingsProvider.GetGameSize() - UIConstants.MinGameSize);
        
        // Layout for Game Size
        // NOTE: Needed a little hack here to get the dialog to display nicely.
        //       I'm not entirely sure why I cannot just set weight to .5 on both
        //        and remove the spacer guy.  But this renders approximately how
        //        I wanted it to anyways.
        LinearLayout gameSizeLayout = new LinearLayout(context);
        gameSizeLayout.addView(
    		gameSizeLabel,
    		LayoutParams.WRAP_CONTENT,
    		LayoutParams.WRAP_CONTENT
		);
        gameSizeLayout.addView(
    		new View(context),
    		new LinearLayout.LayoutParams(1, 1, 0.3f)	
		);
        gameSizeLayout.addView(
    		this.dropdown,
    		new LinearLayout.LayoutParams(1, LayoutParams.WRAP_CONTENT, 0.7f)
		);
        
        // Warning text label.  This is warning that the current game ends when
        //  clicking on preferences.
        TextView warning = new TextView(context);
        warning.setText(context.getString(R.string.preferencesWarning));
        warning.setPadding(10, 10, 10, 10);
                
        // OK button
        // On click of the OK button, the value is saved and the dialog exits
        Button okButton = new Button(context);
        okButton.setText(context.getString(R.string.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SettingsProvider.SetGameSize(
					PreferencesDialog.this.dropdown.getSelectedItemPosition()
					+ UIConstants.MinGameSize
				);
				
				PreferencesDialog.this.dismiss();
			}
        });
        
        // Cancel button
        // Doesn't set the changed value here.
        Button cancelButton = new Button(context);
        cancelButton.setText(context.getString(R.string.cancel));
        cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				PreferencesDialog.this.cancel();
			}
		});
        
        // Layout for the OK and Cancel buttons
        // NOTE: I had to do some weird things to prevent the dialog displaying
        //        weirdly.  Still not sure why this works...
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
    		cancelButton, 
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

        LinearLayout.LayoutParams rootLayout =
    		new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT
			);
        
        // Root layout
        LinearLayout root = new LinearLayout(context);
        root.setLayoutParams(rootLayout);
        root.setOrientation(LinearLayout.VERTICAL);
        
        // Add spacer
        root.addView(this.getSpacerView());

        // Add game size
        root.addView(
    		gameSizeLayout,
    		LayoutParams.FILL_PARENT,
    		LayoutParams.WRAP_CONTENT
		);

        // Add warning text
        root.addView(
    		warning,
    		LayoutParams.WRAP_CONTENT,
    		LayoutParams.WRAP_CONTENT
		);

        // Add spacer
        root.addView(this.getSpacerView());
                
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
