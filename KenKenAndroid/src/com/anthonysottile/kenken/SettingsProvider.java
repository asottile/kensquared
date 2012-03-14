package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

public final class SettingsProvider {

	private static SharedPreferences preferences = null;
	
	private static int gameSize = 4;
	
	private static final String GameSize = "GameSize";
	private static final int DefaultGameSize = 4;
		
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
				SettingsProvider.DefaultGameSize
			);
	}
	
	private static List<IGenericEventHandler> gameSizeChangedHandlers = new ArrayList<IGenericEventHandler>();
	private static void triggerGameSizeChanged() {
		int handlersSize = SettingsProvider.gameSizeChangedHandlers.size();
		for(int i = 0 ; i < handlersSize; i += 1) {
			IGenericEventHandler handler = SettingsProvider.gameSizeChangedHandlers.get(i);
			handler.HandleGenericEvent((Integer)SettingsProvider.gameSize);
		}
	}
	public static void AddGameSizeChangedEventListener(IGenericEventHandler handler) {
		SettingsProvider.gameSizeChangedHandlers.add(handler);
	}
	public static void RemoveGameSizeChangedEventListener(IGenericEventHandler handler) {
		SettingsProvider.gameSizeChangedHandlers.remove(handler);
	} 
	
	private SettingsProvider() { }
}
