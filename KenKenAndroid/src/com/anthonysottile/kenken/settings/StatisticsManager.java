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
	
	/**
	 * Clears the statistics.
	 */
	public static void ClearGameStatistics() {

		// Create dummy JSON array
		JSONArray array = new JSONArray();
		try {
			for (int i = 0; i < 6; i += 1) {
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
			for (int i = 0; i < 6; i += 1) {
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
	
	/**
	 * Loads the statistics.  Populates them if this is the first run.
	 */
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
			
			for (int i = 0; i < 6; i += 1) {
				StatisticsManager.statistics[i] =
					new GameStatistics(array.getJSONObject(i));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Game Statistics for the specified game size.
	 * 
	 * @param gameSize The game size to retrieve statistics for.
	 * @return The Game Statistics for the specified game size.
	 */
	public static GameStatistics GetGameStatistics(int gameSize) {
		if (StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		return StatisticsManager.statistics[gameSize - UIConstants.MinGameSize];
	}
	
	/**
	 * Call when a game has been started to update that statistic.
	 * 
	 * @param gameSize The size of the game started.
	 */
	public static void GameStarted(int gameSize) {
		if (StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		int index = gameSize - UIConstants.MinGameSize;
		StatisticsManager.statistics[index].GameStarted();
		
		StatisticsManager.saveGameStatistics();
	}
	
	/**
	 * Call when a game has ended to update statistics.
	 * Returns true if this is a new high score.
	 * 
	 * @param gameSize The size of the game completed.
	 * @param ticks The number of ms that the game took.
	 * @return True if the game is a new high score.
	 */
	public static boolean GameEnded(int gameSize, long ticks) {

		// Returns true if the game is a high score
		boolean returnValue = false;
		
		if (StatisticsManager.statistics == null) {
			StatisticsManager.Load();
		}
		
		int index = gameSize - UIConstants.MinGameSize;
		GameStatistics game = StatisticsManager.statistics[index];
		
		int gameSeconds = (int)(ticks / 1000);
		
		game.GameWon(gameSeconds);
		
		if (gameSeconds < game.getBestTime()) {
			game.SetBestTime(gameSeconds);
			returnValue = true;
		}
		
		StatisticsManager.saveGameStatistics();
		
		return returnValue;
	}
	
	/**
	 * Initialization method to give the manager a reference to preferences.
	 * 
	 * @param preferences A reference to the application preferences.
	 */
	public static void Initialize(SharedPreferences preferences) {
		StatisticsManager.preferences = preferences;
	}
	
	private StatisticsManager() { }
}