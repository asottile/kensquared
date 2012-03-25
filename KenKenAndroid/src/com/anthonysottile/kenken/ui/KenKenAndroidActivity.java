package com.anthonysottile.kenken.ui;

import java.util.EventObject;

import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.settings.SettingsProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class KenKenAndroidActivity extends Activity {

	private static final String preferences = "com.anthonysottile.kenken";

	private GameComponent gameComponent = null;
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // This is used to give a closure (using JS terminology) reference
        // See below when I instantiate an anonymous interface.
        final KenKenAndroidActivity self = this;
        
        // Give a reference to settings to our static settings manager
        // Also attach to the settings's event handler
        SettingsProvider.Initialize(
    		this.getSharedPreferences(KenKenAndroidActivity.preferences, 0)
		);
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
        
        // Give the gameComponent references to layouts.
        this.gameComponent.Initialize(this.candidatesLayout, this.valuesLayout);
        
        this.gameComponent.AddGameWonListener(
    		new GameComponent.GameWonListener() {
				public void onGameWon(GameComponent.GameWonEvent event) {
					self.showMessageBox("Game Won!");
				}
			}
		);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    private void newGame() {
    	int gameSize = SettingsProvider.GetGameSize();
    	this.gameComponent.NewGame(gameSize);
    	this.candidatesLayout.NewGame(gameSize);
    	this.valuesLayout.NewGame(gameSize);
    }
    
    private void showPreferences() {
    	Intent settingsActivity = new Intent(getBaseContext(), KenKenPreferences.class);
        startActivity(settingsActivity);
    }
    
    private void showStatistics() {
    	this.showMessageBox("Show Statistics Clicked");
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