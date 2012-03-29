package com.anthonysottile.kenken.ui;

import java.util.EventObject;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.settings.StatisticsManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class KenKenAndroidActivity extends Activity {

	private static final String preferences = "com.anthonysottile.kenken";

	private GameComponent gameComponent = null;
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	private TextView timerText = null;
	
	private void showMessageBox(String message) {
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false);
		ad.setMessage(message);
		ad.setButton(this.getString(R.string.ok), new DialogInterface.OnClickListener() {
					
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		ad.show();
	}
	
	private void gameSizeChanged() {
		this.gameComponent.Clear();
		this.candidatesLayout.Clear();
		this.valuesLayout.Clear();
	}
	
	private void gameWon(GameComponent.GameWonEvent event) {
		boolean highScore =
			StatisticsManager.GameEnded(event.getSize(), event.getTicks());
		
		if(highScore) {
			this.showMessageBox("New High Score Win!"); 
		} else {
			this.showMessageBox("Win but no new high score");
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // This is used to give a closure (using JS terminology) reference
        // See below when I instantiate an anonymous interface.
        final KenKenAndroidActivity self = this;
        
        // Give a reference to settings to our static settings manager
        // Also attach to the settings's event handler
        SharedPreferences preferences =
    		this.getSharedPreferences(KenKenAndroidActivity.preferences, 0);
        SettingsProvider.Initialize(preferences);
        StatisticsManager.Initialize(preferences);
        SettingsProvider.AddGameSizeChangedListener(
    		new SettingsProvider.GameSizeChangedListener() {
    			public void onGameSizeChanged(EventObject event) {
    				self.gameSizeChanged();
    			}
    		}
		);
        
        // Set up private references for convenience later
        this.candidatesLayout =
    		(CandidatesLayout)this.findViewById(R.id.candidatesLayout);
        this.valuesLayout =
    		(ValuesLayout)this.findViewById(R.id.valuesLayout);
        this.gameComponent =
    		(GameComponent)this.findViewById(R.id.gameComponent);
        this.timerText =
    		(TextView)this.findViewById(R.id.timerText);
        
        // Give the gameComponent references to layouts.
        this.gameComponent.Initialize(this.candidatesLayout, this.valuesLayout);
        
        this.gameComponent.AddGameWonListener(
    		new GameComponent.GameWonListener() {
				public void onGameWon(GameComponent.GameWonEvent event) {
					self.gameWon(event);
				}
			}
		);
        
        this.timerText.setText("00:00:00");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    private void newGame() {
    	int gameSize = SettingsProvider.GetGameSize();
    	StatisticsManager.GameStarted(gameSize);
    	this.gameComponent.NewGame(gameSize);
    	this.candidatesLayout.NewGame(gameSize);
    	this.valuesLayout.NewGame(gameSize);
    }
    
    private void showPreferences() {
    	Intent settingsActivity = new Intent(getBaseContext(), KenKenPreferences.class);
        startActivity(settingsActivity);
    }
    
    private void showStatistics() {
    	Intent statisticsActivity = new Intent(getBaseContext(), KenKenStatistics.class);
        startActivity(statisticsActivity);
    }
    
    private void showAbout() {
    	this.showMessageBox("Show About Clicked");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newGame:
        		this.newGame();
                return true;
            case R.id.preferences:
        		this.showPreferences();
                return true;
            case R.id.statistics:
        		this.showStatistics();
            	return true;
            case R.id.about:
            	this.showAbout();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}