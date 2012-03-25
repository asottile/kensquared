package com.anthonysottile.kenken.settings;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.anthonysottile.kenken.ui.UIConstants;

import android.content.SharedPreferences;

public final class SettingsProvider {

	private static final String GameSize = "GameSize";
	
	private static SharedPreferences preferences = null;
	
	private static int gameSize = 4;
	
	// TODO: implement dirty cache methodology

	// #region Game Size Changed Event
	
	public interface GameSizeChangedListener extends EventListener {
		public void onGameSizeChanged(EventObject event);
	}
	
	private static List<GameSizeChangedListener> gameSizeChangedListeners =
		new ArrayList<GameSizeChangedListener>();
	public static void AddGameSizeChangedListener(GameSizeChangedListener listener) {
		SettingsProvider.gameSizeChangedListeners.add(listener);
	}
	public static void RemoveGameSizeChangedListener(GameSizeChangedListener listener) {
		SettingsProvider.gameSizeChangedListeners.remove(listener);
	}
	private static void triggerGameSizeChanged() {
		EventObject event = new EventObject(new Object());
		
		int size = SettingsProvider.gameSizeChangedListeners.size();
		for(int i = 0; i < size; i += 1) { 
			SettingsProvider.gameSizeChangedListeners.get(i).onGameSizeChanged(event);
		}
	}
	
	// #endregion
	
	public static int GetGameSize() {
		return SettingsProvider.gameSize;
	}
	public static void SetGameSize(int gameSize) {
		if(gameSize != SettingsProvider.gameSize) {
			SettingsProvider.gameSize = gameSize;
			SharedPreferences.Editor editor = SettingsProvider.preferences.edit();
			editor.putInt(SettingsProvider.GameSize, SettingsProvider.gameSize);
			editor.commit();
			
			SettingsProvider.triggerGameSizeChanged();
		}
	}
	
	public static void Initialize(SharedPreferences preferences) {
		SettingsProvider.preferences = preferences;
		SettingsProvider.gameSize =
			SettingsProvider.preferences.getInt(
				SettingsProvider.GameSize,
				UIConstants.MinimumGameSize
			);
	}
	
	private SettingsProvider() { }
}