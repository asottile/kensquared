package com.anthonysottile.kenken.ui;

import java.util.EventObject;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.settings.StatisticsManager;
import com.anthonysottile.kenken.ui.GameComponent.GameWonEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class KenKenAndroidActivity extends Activity {

	private static final int PreferencesDialogId = 0;
	private static final int StatisticsDialogId = 1;
	
	private static final String preferences = "com.anthonysottile.kenken";

	private GameComponent gameComponent = null;
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	private TextView timerText = null;
	
	private boolean inGame = false;
	
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
		this.inGame = false;
		this.gameComponent.Clear();
		this.candidatesLayout.Clear();
		this.valuesLayout.Clear();
	}
	
	private GameComponent.GameWonListener gameWonListener =
		new GameComponent.GameWonListener() {
			public void onGameWon(GameWonEvent event) {

				KenKenAndroidActivity.this.inGame = false;
				
				boolean highScore =
					StatisticsManager.GameEnded(
						event.getSize(), 
						event.getTicks()
					);
				
				if(highScore) {
					KenKenAndroidActivity.this.showMessageBox("New High Score Win!");
				} else {
					KenKenAndroidActivity.this.showMessageBox("Win but no new high score");
				}
			}		
		};
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
                
        // Give a reference to settings to our static settings manager
        // Also attach to the settings's event handler
        SharedPreferences preferences =
    		this.getSharedPreferences(KenKenAndroidActivity.preferences, 0);
        SettingsProvider.Initialize(preferences);
        StatisticsManager.Initialize(preferences);
        SettingsProvider.AddGameSizeChangedListener(
    		new SettingsProvider.GameSizeChangedListener() {
    			public void onGameSizeChanged(EventObject event) {
    				KenKenAndroidActivity.this.gameSizeChanged();
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
        this.gameComponent.Initialize(
    		this.candidatesLayout,
    		this.valuesLayout,
    		this.timerText
		);
        
        this.gameComponent.AddGameWonListener(this.gameWonListener);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// See if we have a saved game to restore
    	this.gameComponent.LoadState(SettingsProvider.GetSavedGame());
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	SettingsProvider.SaveGame(this.gameComponent.SaveState());
    }
    
    
    private void newGame() {
    	this.inGame = true;
    	
    	int gameSize = SettingsProvider.GetGameSize();
    	StatisticsManager.GameStarted(gameSize);
    	this.gameComponent.NewGame(gameSize);
    	this.candidatesLayout.NewGame(gameSize);
    	this.valuesLayout.NewGame(gameSize);
    }
    
    private void pauseGame() {
    	this.gameComponent.TogglePause();
    }
    
    private void checkGame() {
    	
    }
    
    private void showPreferences() {
    	this.gameComponent.SetPausedIfNotPaused();
    	this.showDialog(KenKenAndroidActivity.PreferencesDialogId);
    }
    
    private void showStatistics() {
    	this.gameComponent.SetPausedIfNotPaused();
    	Intent statisticsActivity =
			new Intent(getBaseContext(), KenKenStatistics.class);
        startActivity(statisticsActivity);
    }
    
    private void showAbout() {
    	this.showMessageBox("Show About Clicked");
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	
    	Dialog dialog;
    	switch(id) {
    		case KenKenAndroidActivity.PreferencesDialogId:
    			
    			dialog = new PreferencesDialog(this);
    			
    			break;
    		case KenKenAndroidActivity.StatisticsDialogId:
    			
    			dialog = new StatisticsDialog(this);
    			
    			break;
			default:
				dialog = null;
    	}
    	
    	return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	
    	switch(id) {
    		case KenKenAndroidActivity.PreferencesDialogId:
    			
    			PreferencesDialog d = (PreferencesDialog)dialog;
    			d.SetSpinner(SettingsProvider.GetGameSize());
    			
    			break;
    		case KenKenAndroidActivity.StatisticsDialogId:
    			
    			break;
			default:
				break;	
    	}
    }
    
    // #region Menu
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	menu.findItem(R.id.check).setEnabled(this.inGame);
    	menu.findItem(R.id.pause).setEnabled(this.inGame);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newGame:
        		this.newGame();
                return true;
            case R.id.pause:
            	this.pauseGame();
            	return true;
            case R.id.check:
            	this.checkGame();
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
    
    // #endregion
    
}