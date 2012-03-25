package com.anthonysottile.kenken.settings;

import org.json.JSONArray;
import org.json.JSONException;

import com.anthonysottile.kenken.ui.UIConstants;

import android.content.SharedPreferences;

public final class StatisticsManager {

	private static final String Statistics = "Statistics";
	// TODO: implement dirty cache methodology
	
	private static SharedPreferences preferences = null;
	private static GameStatistics[] statistics = null;
	
	public static void ClearGameStatistics() {

		// Create dummy JSON array
		JSONArray array = new JSONArray();
		try {
			for(int i = 0; i < 6; i += 1) {
				array.put(i, new GameStatistics(i + 4).ToJson());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		SharedPreferences.Editor editor =
			StatisticsManager.preferences.edit();
		
		editor.putString(StatisticsManager.Statistics, array.toString());
		editor.commit();
		
		StatisticsManager.statistics = null;
	}
	
	private static void saveGameStatistics() {

		// Create JSON array
		JSONArray array = new JSONArray();
		try {
			for(int i = 0; i < 6; i += 1) {
				array.put(i, StatisticsManager.statistics[i].ToJson());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		SharedPreferences.Editor editor =
			StatisticsManager.preferences.edit();
		
		editor.putString(StatisticsManager.Statistics, array.toString());
		editor.commit();
	}
	
	public static void Load() {
		// First check if we have the Statistics already there
		if (!StatisticsManager.preferences.contains(StatisticsManager.Statistics)) {
			StatisticsManager.ClearGameStatistics();
		}
		
		StatisticsManager.statistics = new GameStatistics[6];
		
		try {
			JSONArray array =
				new JSONArray(
					StatisticsManager.preferences.getString(
						StatisticsManager.Statistics,
						""
					)
				);
			
			for(int i = 0; i < 6; i += 1) {
				StatisticsManager.statistics[i] =
					new GameStatistics(array.getJSONObject(i));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static GameStatistics GetGameStatistics(int gameSize) {
		
		if(StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		return StatisticsManager.statistics[gameSize - UIConstants.MinimumGameSize];
	}
	
	public static void GameStarted(int gameSize) {
		if(StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		int index = gameSize - UIConstants.MinimumGameSize;
		StatisticsManager.statistics[index].GameStarted();
		
		StatisticsManager.saveGameStatistics();
	}
	
	public static boolean GameEnded(int gameSize, long ticks) {

		// Returns true if the game is a high score
		boolean returnValue = false;
		
		if(StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		int index = gameSize - UIConstants.MinimumGameSize;
		GameStatistics game = StatisticsManager.statistics[index];
		
		int gameSeconds = (int)(ticks / 1000);
		
		game.GameWon(gameSeconds);
		
		if(gameSeconds < game.getBestTime()) {
			game.SetBestTime(gameSeconds);
			returnValue = true;
		}
		
		StatisticsManager.saveGameStatistics();
		
		return returnValue;
	}
	
	public static void Initialize(SharedPreferences preferences) {
		StatisticsManager.preferences = preferences;
	}
	
	private StatisticsManager() { }
}