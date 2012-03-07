package com.anthonysottile.kenken;

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
	
	private SettingsProvider() { }
}
