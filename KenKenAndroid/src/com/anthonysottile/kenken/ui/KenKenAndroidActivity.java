package com.anthonysottile.kenken.ui;

import java.util.EventObject;

import org.json.JSONException;
import org.json.JSONObject;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.settings.StatisticsManager;
import com.anthonysottile.kenken.ui.GameComponent.GameState;
import com.anthonysottile.kenken.ui.GameComponent.GameWonEvent;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class KenKenAndroidActivity extends Activity {

	private static final int PreferencesDialogId = 0;
	private static final int StatisticsDialogId = 1;
	private static final int GameWonDialogId = 2;
	private static final int AboutDialogId = 3;

	private static final String preferences = "com.anthonysottile.kenken";
	private static final String saveGameBundleProperty = "SavedGame";

	private GameComponent gameComponent = null;
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	private TextView timerText = null;

	private int gameWonGameSize = 4;
	private boolean gameWonNewHighScore = false;
	private long gameWonTicks = -1;
	
	private void gameSizeChanged() {
		this.gameComponent.Clear();
	}
	
	private GameComponent.GameWonListener gameWonListener =
		new GameComponent.GameWonListener() {
			public void onGameWon(GameWonEvent event) {

				KenKenAndroidActivity.this.gameWonNewHighScore =
					StatisticsManager.GameEnded(
						event.getSize(), 
						event.getTicks()
					);
				
				KenKenAndroidActivity.this.gameWonGameSize = event.getSize();
				KenKenAndroidActivity.this.gameWonTicks = event.getTicks();
				
				KenKenAndroidActivity.this.showDialog(
					KenKenAndroidActivity.GameWonDialogId
				);
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
        
        // Give a reference to resources for Bitmap cache
        BitmapCache.Initialize(this.getResources());
        
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
        
        // Restore the saved state if applicable
        if (savedInstanceState != null) {
        	if (savedInstanceState.containsKey(KenKenAndroidActivity.saveGameBundleProperty)) {
        		
        		String gameJsonString =
    				savedInstanceState.getString(KenKenAndroidActivity.saveGameBundleProperty);
        		
        		if (gameJsonString.length() > 0) {
        			try {
        				JSONObject gameAsJson = new JSONObject(gameJsonString);
        	    		this.gameComponent.LoadState(gameAsJson);
        			} catch (JSONException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

    	// Pause the game since they are navigating away
    	this.gameComponent.PauseIfNotPaused();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	JSONObject game = this.gameComponent.SaveState();
    	if (game != null) {
        	savedInstanceState.putString(
    			KenKenAndroidActivity.saveGameBundleProperty,
    			game.toString()
			);
    	}
    }
    
    private void newGame() {
    	int gameSize = SettingsProvider.GetGameSize();
    	StatisticsManager.GameStarted(gameSize);
    	this.gameComponent.NewGame(gameSize);
    }
    
    private void pauseGame() {
    	this.gameComponent.TogglePause();
    }
    
    private void checkGame() {
    	this.gameComponent.Check();
    }
    
    private void showPreferences() {
    	this.gameComponent.PauseIfNotPaused();
    	this.showDialog(KenKenAndroidActivity.PreferencesDialogId);
    }
    
    private void showStatistics() {
    	this.gameComponent.PauseIfNotPaused();
    	this.showDialog(KenKenAndroidActivity.StatisticsDialogId);
    }
    
    private void showAbout() {
    	this.gameComponent.PauseIfNotPaused();
    	this.showDialog(KenKenAndroidActivity.AboutDialogId);
    }
    
    // #region Dialogs
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	
    	Dialog dialog;
    	switch (id) {
    		case KenKenAndroidActivity.PreferencesDialogId:
    			dialog = new PreferencesDialog(this);
    			break;
    		case KenKenAndroidActivity.StatisticsDialogId:
    			dialog = new StatisticsDialog(this);
    			break;
    		case KenKenAndroidActivity.GameWonDialogId:
    			dialog = new GameWonDialog(this);
    			break;
    		case KenKenAndroidActivity.AboutDialogId:
    			dialog = new AboutDialog(this);
    			break;
			default:
				dialog = null;
    	}
    	
    	return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	
    	switch (id) {
    		case KenKenAndroidActivity.PreferencesDialogId:
    			
    			((PreferencesDialog)dialog).SetSpinner(SettingsProvider.GetGameSize());
    			
    			break;
    		case KenKenAndroidActivity.StatisticsDialogId:
    			
    			((StatisticsDialog)dialog).Refresh();
    			
    			break;
    			
    		case KenKenAndroidActivity.GameWonDialogId:
    			
    			((GameWonDialog)dialog).Setup(
					this.gameWonGameSize,
					this.gameWonNewHighScore,
					this.gameWonTicks
				);
    			
    			break;
    		case KenKenAndroidActivity.AboutDialogId:
			default:
				break;	
    	}
    }
    
    // #endregion
    
    // #region Menus
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
    	GameComponent.GameState gameState = this.gameComponent.getGameState();
    	menu.findItem(R.id.check).setEnabled(gameState == GameState.InGame);
    	menu.findItem(R.id.pause).setEnabled(
			gameState == GameState.InGame || gameState == GameState.Paused
		);
    	if (gameState != GameState.Paused) {
    		menu.findItem(R.id.pause).setTitle(R.string.pause);
    	} else {
    		menu.findItem(R.id.pause).setTitle(R.string.resume);
    	}
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