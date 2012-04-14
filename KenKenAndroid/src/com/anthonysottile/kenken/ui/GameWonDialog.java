package com.anthonysottile.kenken.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.GameStatistics;
import com.anthonysottile.kenken.settings.StatisticsManager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameWonDialog extends Dialog {

	private TextView newHighScore = null;
	private TextView winTime = null;
	private TextView winDate = null;
	
	private TextView gameSize = null;
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
        spacerView.setLayoutParams(spacerViewLayoutParams);
        return spacerView;
	}

	private void setValues(int gameSize) {
		GameStatistics statistics = StatisticsManager.GetGameStatistics(gameSize);
		
		this.gamesPlayed.setText(Integer.toString(statistics.getGamesPlayed(), 10));
		this.gamesWon.setText(Integer.toString(statistics.getGamesWon(), 10));
		
		this.averageTime.setText(
			GameWonDialog.toTimeString(
				statistics.getTotalSeconds() / statistics.getGamesWon()
			)
		);
			
		this.bestTime.setText(
				GameWonDialog.toTimeString(
				statistics.getBestTime()
			)
		);
			
		this.bestTimeDate.setText(dateFormat.format(statistics.getBestTimeDate()));
	}

	public void Setup(int gameSize, boolean newHighScore, long ticks) {
		
		this.setValues(gameSize);
		
		this.gameSize.setText(Integer.toString(gameSize, 10));
		this.winTime.setText(GameWonDialog.toTimeString((int)ticks / 1000));
		this.winDate.setText(dateFormat.format(new Date()));
		
		if (newHighScore) {
			this.newHighScore.setText(this.getContext().getString(R.string.newHighScore));
		} else {
			this.newHighScore.setText("");
		}
	}
	
	public GameWonDialog(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context context = this.getContext();
        
		this.setTitle(context.getString(R.string.youWin));
            
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
        
        this.newHighScore = new TextView(context);
        this.newHighScore.setTextColor(Color.WHITE);
        this.newHighScore.setPadding(
    		UIConstants.StatisticsOneIndent * 2,
    		5,
    		UIConstants.StatisticsOneIndent * 2,
    		0
		);
        
        root.addView(this.newHighScore, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView winTimeLabel = new TextView(context);
        winTimeLabel.setText(context.getString(R.string.gameTime));
        winTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.winTime = new TextView(context);
        this.winTime.setPadding(5, 5, 5, 5);
        
        LinearLayout winTimeLayout = new LinearLayout(context);
        winTimeLayout.addView(winTimeLabel, GameWonDialog.wrapContent);
        winTimeLayout.addView(new View(context), GameWonDialog.middleSpacer);
        winTimeLayout.addView(this.winTime,  GameWonDialog.wrapContent);
        winTimeLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(winTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView winDateLabel = new TextView(context);
        winDateLabel.setText(context.getString(R.string.date));
        winDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.winDate = new TextView(context);
        this.winDate.setPadding(5, 5, 5, 5);
        
        LinearLayout winDateLayout = new LinearLayout(context);
        winDateLayout.addView(winDateLabel, GameWonDialog.wrapContent);
        winDateLayout.addView(new View(context), GameWonDialog.middleSpacer);
        winDateLayout.addView(this.winDate,  GameWonDialog.wrapContent);
        winDateLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(winDateLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        root.addView(this.getSpacerView());

        TextView gameSizeLabel = new TextView(context);
        gameSizeLabel.setText(context.getString(R.string.gameSizeColon));
        gameSizeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gameSize = new TextView(context);
        this.gameSize.setPadding(5, 5, 5, 5);
        
        LinearLayout gameSizeLayout = new LinearLayout(context);
        gameSizeLayout.addView(gameSizeLabel, GameWonDialog.wrapContent);
        gameSizeLayout.addView(new View(context), GameWonDialog.middleSpacer);
        gameSizeLayout.addView(this.gameSize,  GameWonDialog.wrapContent);
        gameSizeLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(gameSizeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView gamesPlayedLabel = new TextView(context);
        gamesPlayedLabel.setText(context.getString(R.string.gamesPlayed));
        gamesPlayedLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesPlayed = new TextView(context);
        this.gamesPlayed.setPadding(5, 5, 5, 5);
        
        LinearLayout gamesPlayedLayout = new LinearLayout(context);
        gamesPlayedLayout.addView(gamesPlayedLabel, GameWonDialog.wrapContent);
        gamesPlayedLayout.addView(new View(context), GameWonDialog.middleSpacer);
        gamesPlayedLayout.addView(this.gamesPlayed,  GameWonDialog.wrapContent);
        gamesPlayedLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(gamesPlayedLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView gamesWonLabel = new TextView(context);
        gamesWonLabel.setText(context.getString(R.string.gamesWon));
        gamesWonLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesWon = new TextView(context);
        this.gamesWon.setPadding(5, 5, 5, 5);
        
        LinearLayout gamesWonLayout = new LinearLayout(context);
        gamesWonLayout.addView(gamesWonLabel, GameWonDialog.wrapContent);
        gamesWonLayout.addView(new View(context), GameWonDialog.middleSpacer);
        gamesWonLayout.addView(this.gamesWon, GameWonDialog.wrapContent);
        gamesWonLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(gamesWonLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView averageTimeLabel = new TextView(context);
        averageTimeLabel.setText(context.getString(R.string.averageTime));
        averageTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.averageTime = new TextView(context);
        this.averageTime.setPadding(5, 5, 5, 5);
        
        LinearLayout averageTimeLayout = new LinearLayout(context);
        averageTimeLayout.addView(averageTimeLabel, GameWonDialog.wrapContent);
        averageTimeLayout.addView(new View(context), GameWonDialog.middleSpacer);
        averageTimeLayout.addView(this.averageTime, GameWonDialog.wrapContent);
        averageTimeLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(averageTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView bestTimeLabel = new TextView(context);
        bestTimeLabel.setText(context.getString(R.string.bestTime));
        bestTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTime = new TextView(context);
        this.bestTime.setPadding(5, 5, 5, 5);
        
        LinearLayout bestTimeLayout = new LinearLayout(context);
        bestTimeLayout.addView(bestTimeLabel, GameWonDialog.wrapContent);
        bestTimeLayout.addView(new View(context), GameWonDialog.middleSpacer);
        bestTimeLayout.addView(this.bestTime, GameWonDialog.wrapContent);
        bestTimeLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(bestTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView bestTimeDateLabel = new TextView(context);
        bestTimeDateLabel.setText(context.getString(R.string.bestTimeDate));
        bestTimeDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTimeDate = new TextView(context);
        this.bestTimeDate.setPadding(5, 5, 5, 5);
        
        LinearLayout bestTimeDateLayout = new LinearLayout(context);
        bestTimeDateLayout.addView(bestTimeDateLabel, GameWonDialog.wrapContent);
        bestTimeDateLayout.addView(new View(context), GameWonDialog.middleSpacer);
        bestTimeDateLayout.addView(this.bestTimeDate, GameWonDialog.wrapContent);
        bestTimeDateLayout.addView(new View(context), GameWonDialog.rightPad);
        root.addView(bestTimeDateLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        root.addView(this.getSpacerView());
        
        // OK button
        // On click of the OK button the dialog exits
        Button okButton = new Button(context);
        okButton.setText(context.getString(R.string.ok));
        okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {				
				GameWonDialog.this.dismiss();
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
