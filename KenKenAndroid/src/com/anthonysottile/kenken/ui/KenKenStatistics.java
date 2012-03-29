package com.anthonysottile.kenken.ui;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.GameStatistics;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.settings.StatisticsManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class KenKenStatistics extends Activity {

	private Spinner dropdown = null;
	private TextView gamesPlayed = null;
	private TextView gamesWon = null;
	private TextView averageTime = null;
	private TextView bestTime = null;
	private TextView bestTimeDate = null;
	
	private static final SimpleDateFormat dateFormat =
		new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	
	private static final String[] GameSizes = {
		"4", "5", "6", "7", "8", "9"
	};
	
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
	
	private View getSpacerView() {
		View spacerView = new View(this);
        spacerView.setBackgroundColor(Color.LTGRAY);
        return spacerView;
	}
	
	private void setValues(int gameSize) {
		GameStatistics statistics = StatisticsManager.GetGameStatistics(gameSize);
		
		this.gamesPlayed.setText(Integer.toString(statistics.getGamesPlayed(), 10));
		this.gamesWon.setText(Integer.toString(statistics.getGamesWon(), 10));
		
		if(statistics.getGamesWon() > 0) {
			this.averageTime.setText(
				KenKenStatistics.toTimeString(
					statistics.getTotalSeconds() / statistics.getGamesWon()
				)
			);
			
			this.bestTime.setText(
				KenKenStatistics.toTimeString(
					statistics.getBestTime()
				)
			);
			
			this.bestTimeDate.setText(dateFormat.format(statistics.getBestTimeDate()));
			
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
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final KenKenStatistics self = this;
        
        LinearLayout.LayoutParams halfWidth =
    		new LinearLayout.LayoutParams(
    			30,
    			LayoutParams.WRAP_CONTENT
			);
        halfWidth.weight = 0.5f;
            
        LinearLayout.LayoutParams rootLayout =
    		new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT
			);
        LinearLayout root = new LinearLayout(this);
        root.setLayoutParams(rootLayout);
        root.setOrientation(LinearLayout.VERTICAL);
        
        TextView statisticsLabel = new TextView(this);
        statisticsLabel.setTextColor(Color.LTGRAY);
        statisticsLabel.setText(this.getString(R.string.statistics));
        statisticsLabel.setTextSize(20);
        statisticsLabel.setPadding(5, 5, 5, 5);
        root.addView(
    		statisticsLabel,
    		LayoutParams.FILL_PARENT,
    		LayoutParams.WRAP_CONTENT
		);
        
        root.addView(
    		this.getSpacerView(),
    		LayoutParams.FILL_PARENT,
    		1
		);
        
        TextView gameSizeLabel = new TextView(this);
        gameSizeLabel.setText(this.getString(R.string.gameSize));
        gameSizeLabel.setTextSize(18);
        gameSizeLabel.setPadding(UIConstants.StatisticsOneIndent, 5, 5, 5);
        
        this.dropdown = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter =
    		new ArrayAdapter<String>(
				this,
				android.R.layout.simple_spinner_item,
				KenKenStatistics.GameSizes
			);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        this.dropdown.setAdapter(spinnerAdapter);
        this.dropdown.setSelection(SettingsProvider.GetGameSize() - UIConstants.MinGameSize);
        this.dropdown.setOnItemSelectedListener(
    		new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapter, View dropdown,
					int selectedIndex, long id) {
					self.setValues(selectedIndex + UIConstants.MinGameSize);
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// Woo!!!! Do Nothing!!!
				}
    		}
		);

        LinearLayout gameSizeLayout = new LinearLayout(this);
        gameSizeLayout.addView(gameSizeLabel, halfWidth);
        gameSizeLayout.addView(this.dropdown, halfWidth);
        
        root.addView(gameSizeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        root.addView(
    		this.getSpacerView(),
    		LayoutParams.FILL_PARENT,
    		1
		);
        
        TextView gamesPlayedLabel = new TextView(this);
        gamesPlayedLabel.setText(this.getString(R.string.gamesPlayed));
        gamesPlayedLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesPlayed = new TextView(this);
        this.gamesPlayed.setPadding(5, 5, 5, 5);
        
        LinearLayout gamesPlayedLayout = new LinearLayout(this);
        gamesPlayedLayout.addView(gamesPlayedLabel, halfWidth);
        gamesPlayedLayout.addView(this.gamesPlayed, halfWidth);
        root.addView(gamesPlayedLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView gamesWonLabel = new TextView(this);
        gamesWonLabel.setText(this.getString(R.string.gamesWon));
        gamesWonLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.gamesWon = new TextView(this);
        this.gamesWon.setPadding(5, 5, 5, 5);
        
        LinearLayout gamesWonLayout = new LinearLayout(this);
        gamesWonLayout.addView(gamesWonLabel, halfWidth);
        gamesWonLayout.addView(this.gamesWon, halfWidth);
        root.addView(gamesWonLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView averageTimeLabel = new TextView(this);
        averageTimeLabel.setText(this.getString(R.string.averageTime));
        averageTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.averageTime = new TextView(this);
        this.averageTime.setPadding(5, 5, 5, 5);
        
        LinearLayout averageTimeLayout = new LinearLayout(this);
        averageTimeLayout.addView(averageTimeLabel, halfWidth);
        averageTimeLayout.addView(this.averageTime, halfWidth);
        root.addView(averageTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView bestTimeLabel = new TextView(this);
        bestTimeLabel.setText(this.getString(R.string.bestTime));
        bestTimeLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTime = new TextView(this);
        this.bestTime.setPadding(5, 5, 5, 5);
        
        LinearLayout bestTimeLayout = new LinearLayout(this);
        bestTimeLayout.addView(bestTimeLabel, halfWidth);
        bestTimeLayout.addView(this.bestTime, halfWidth);
        root.addView(bestTimeLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        TextView bestTimeDateLabel = new TextView(this);
        bestTimeDateLabel.setText(this.getString(R.string.bestTimeDate));
        bestTimeDateLabel.setPadding(UIConstants.StatisticsOneIndent * 2, 5, 5, 5);
        this.bestTimeDate = new TextView(this);
        this.bestTimeDate.setPadding(5, 5, 5, 5);
        
        LinearLayout bestTimeDateLayout = new LinearLayout(this);
        bestTimeDateLayout.addView(bestTimeDateLabel, halfWidth);
        bestTimeDateLayout.addView(this.bestTimeDate, halfWidth);
        root.addView(bestTimeDateLayout, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        root.addView(
    		this.getSpacerView(),
    		LayoutParams.FILL_PARENT,
    		1
		);
                
        this.setValues(SettingsProvider.GetGameSize());
        
        this.setContentView(root);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.statistics_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clearStatistics:
        		this.clearStatistics();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
