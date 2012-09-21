package com.anthonysottile.kenken.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.GameStatistics;
import com.anthonysottile.kenken.settings.StatisticsManager;

public class StatisticsDialog extends Dialog {

	private static final String[] GameSizes = {
		"4", "5", "6", "7", "8", "9"
	};

	private Spinner dropdown = null;
	private TextView gamesPlayed = null;
	private TextView gamesWon = null;
	private TextView averageTime = null;
	private TextView bestTime = null;
	private TextView bestTimeDate = null;

	private static final SimpleDateFormat dateFormat =
		new SimpleDateFormat("yyyy-MM-dd", Locale.US);

	private static String toTimeString(int time) {

		int seconds = time % 60;
		int minutes = (time / 60) % 60;
		int hours = time / 3600;

		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%02d", hours));
		builder.append(':');
		builder.append(String.format("%02d", minutes));
		builder.append(':');
		builder.append(String.format("%02d", seconds));

		return builder.toString();
	}

	private static final LinearLayout.LayoutParams middleSpacer =
		new LinearLayout.LayoutParams(1, 1, 0.1f);

	private static final LinearLayout.LayoutParams rightPad =
		new LinearLayout.LayoutParams(UIConstants.StatisticsOneIndent * 2, 1);

	private static final LinearLayout.LayoutParams wrapContent =
		new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT,
			LayoutParams.WRAP_CONTENT
		);

	private static final LinearLayout.LayoutParams spacerViewLayoutParams =
		new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
	private View getSpacerView() {
		View spacerView = new View(this.getContext());
        spacerView.setBackgroundColor(Color.LTGRAY);
        spacerView.setLayoutParams(StatisticsDialog.spacerViewLayoutParams);
        return spacerView;
	}

	private void setValues(int gameSize) {
		GameStatistics statistics = StatisticsManager.GetGameStatistics(gameSize);

		this.gamesPlayed.setText(Integer.toString(statistics.getGamesPlayed(), 10));
		this.gamesWon.setText(Integer.toString(statistics.getGamesWon(), 10));

		if (statistics.getGamesWon() > 0) {
			this.averageTime.setText(
				StatisticsDialog.toTimeString(
					statistics.getTotalSeconds() / statistics.getGamesWon()
				)
			);

			this.bestTime.setText(
				StatisticsDialog.toTimeString(
					statistics.getBestTime()
				)
			);

			this.bestTimeDate.setText(StatisticsDialog.dateFormat.format(statistics.getBestTimeDate()));

		} else {
			this.averageTime.setText("");
			this.bestTime.setText("");
			this.bestTimeDate.setText("");
		}
	}

	private void clearStatistics() {
		StatisticsManager.ClearGameStatistics();
		this.setValues(this.dropdown.getSelectedItemPosition() + UIConstants.MinGameSize);
	}

	/**
	 * Refresh the display of the Statistics Dialog.  This is so when showing
	 *  the dialog again after the first show it is updated with whatever
	 *  games were played.
	 */
	public void Refresh() {
		this.setValues(this.dropdown.getSelectedItemPosition() + UIConstants.MinGameSize);
	}

	public StatisticsDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this.getContext();

		this.setTitle(context.getString(R.string.statistics));

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
				StatisticsDialog.GameSizes
			);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.dropdown.setAdapter(spinnerAdapter);
        this.dropdown.setSelection(0);
        this.dropdown.setOnItemSelectedListener(
    		new OnItemSelectedListener() {

    			public void onItemSelected(AdapterView<?> adapter, View view,
    					int selectedIndex, long id) {
    				StatisticsDialog.this.setValues(
						UIConstants.MinGameSize + selectedIndex
					);
    			}

    			public void onNothingSelected(AdapterView<?> arg0) { }
    		}
		);

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

        root.addView(gameSizeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        root.addView(this.getSpacerView());

        TextView gamesPlayedLabel = new TextView(context);
        gamesPlayedLabel.setText(context.getString(R.string.gamesPlayed));
        gamesPlayedLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesPlayed = new TextView(context);
        this.gamesPlayed.setPadding(5, 5, 5, 5);

        LinearLayout gamesPlayedLayout = new LinearLayout(context);
        gamesPlayedLayout.addView(gamesPlayedLabel, StatisticsDialog.wrapContent);
        gamesPlayedLayout.addView(new View(context), StatisticsDialog.middleSpacer);
        gamesPlayedLayout.addView(this.gamesPlayed,  StatisticsDialog.wrapContent);
        gamesPlayedLayout.addView(new View(context), StatisticsDialog.rightPad);
        root.addView(gamesPlayedLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        TextView gamesWonLabel = new TextView(context);
        gamesWonLabel.setText(context.getString(R.string.gamesWon));
        gamesWonLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesWon = new TextView(context);
        this.gamesWon.setPadding(5, 5, 5, 5);

        LinearLayout gamesWonLayout = new LinearLayout(context);
        gamesWonLayout.addView(gamesWonLabel, StatisticsDialog.wrapContent);
        gamesWonLayout.addView(new View(context), StatisticsDialog.middleSpacer);
        gamesWonLayout.addView(this.gamesWon, StatisticsDialog.wrapContent);
        gamesWonLayout.addView(new View(context), StatisticsDialog.rightPad);
        root.addView(gamesWonLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        TextView averageTimeLabel = new TextView(context);
        averageTimeLabel.setText(context.getString(R.string.averageTime));
        averageTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.averageTime = new TextView(context);
        this.averageTime.setPadding(5, 5, 5, 5);

        LinearLayout averageTimeLayout = new LinearLayout(context);
        averageTimeLayout.addView(averageTimeLabel, StatisticsDialog.wrapContent);
        averageTimeLayout.addView(new View(context), StatisticsDialog.middleSpacer);
        averageTimeLayout.addView(this.averageTime, StatisticsDialog.wrapContent);
        averageTimeLayout.addView(new View(context), StatisticsDialog.rightPad);
        root.addView(averageTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        TextView bestTimeLabel = new TextView(context);
        bestTimeLabel.setText(context.getString(R.string.bestTime));
        bestTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTime = new TextView(context);
        this.bestTime.setPadding(5, 5, 5, 5);

        LinearLayout bestTimeLayout = new LinearLayout(context);
        bestTimeLayout.addView(bestTimeLabel, StatisticsDialog.wrapContent);
        bestTimeLayout.addView(new View(context), StatisticsDialog.middleSpacer);
        bestTimeLayout.addView(this.bestTime, StatisticsDialog.wrapContent);
        bestTimeLayout.addView(new View(context), StatisticsDialog.rightPad);
        root.addView(bestTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        TextView bestTimeDateLabel = new TextView(context);
        bestTimeDateLabel.setText(context.getString(R.string.bestTimeDate));
        bestTimeDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTimeDate = new TextView(context);
        this.bestTimeDate.setPadding(5, 5, 5, 5);

        LinearLayout bestTimeDateLayout = new LinearLayout(context);
        bestTimeDateLayout.addView(bestTimeDateLabel, StatisticsDialog.wrapContent);
        bestTimeDateLayout.addView(new View(context), StatisticsDialog.middleSpacer);
        bestTimeDateLayout.addView(this.bestTimeDate, StatisticsDialog.wrapContent);
        bestTimeDateLayout.addView(new View(context), StatisticsDialog.rightPad);
        root.addView(bestTimeDateLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        root.addView(this.getSpacerView());

        // OK button
        // On click of the OK button the dialog exits
        Button okButton = new Button(context);
        okButton.setText(context.getString(R.string.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				StatisticsDialog.this.dismiss();
			}
        });

        // Cancel button
        // Doesn't set the changed value here.
        Button clearButton = new Button(context);
        clearButton.setText(R.string.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		AlertDialog.Builder builder = new AlertDialog.Builder(StatisticsDialog.this.getContext());
        		builder.setMessage(R.string.confirmClear)
        		.setCancelable(false)
        		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				StatisticsDialog.this.clearStatistics();
        				dialog.dismiss();
        			}
        		})
        		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				dialog.dismiss();
        			}
        		});
        		AlertDialog alert = builder.create();
        		alert.show();
			}
		});

        // Layout for the OK and Clear buttons
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
    		clearButton,
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
