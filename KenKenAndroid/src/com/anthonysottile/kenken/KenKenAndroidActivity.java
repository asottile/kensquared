package com.anthonysottile.kenken;

import com.anthonysottile.kenken.ui.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class KenKenAndroidActivity extends Activity {

	private static final String Preferences = "com.anthonysottile.kenken";
	
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Give a reference to settings
        SettingsProvider.Initialize(this.getSharedPreferences(Preferences, 0));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    private void newGame() {
    	((GameComponent)this.findViewById(R.id.gameComponent))
    		.NewGame(SettingsProvider.GetGameSize());
    	
    	((CandidatesLayout)this.findViewById(R.id.candidatesLayout))
    		.NewGame(SettingsProvider.GetGameSize());
    	
    	// TODO: handle game size changed event at this level instead of at each level
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