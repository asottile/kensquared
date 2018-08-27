package com.anthonysottile.kenken.settings;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class GameStatistics
{
	// NOTE: According to http://developer.android.com/reference/java/util/Locale.html
	//        Locale.US should be used like C#  CultureInfo.InvariantCulture
	private static final DateFormat dateFormatter =
		SimpleDateFormat.getDateInstance(
			SimpleDateFormat.LONG,
			Locale.US
		);

	private int gameSize = 0;
	public int getGameSize() {
		return this.gameSize;
	}

	private int gamesPlayed = 0;
	public int getGamesPlayed() {
		return this.gamesPlayed;
	}

	private int gamesWon = 0;
	public int getGamesWon() {
		return this.gamesWon;
	}

	private int totalSeconds = 0;
	public int getTotalSeconds() {
		return this.totalSeconds;
	}

	private int bestTime = Integer.MAX_VALUE;
	public int getBestTime() {
		return this.bestTime;
	}

	private Date bestTimeDate = null;
	public Date getBestTimeDate() {
		return this.bestTimeDate;
	}

	public void GameStarted() {
		this.gamesPlayed += 1;
	}

	public void GameWon(int time) {
		this.gamesWon += 1;
		this.totalSeconds += time;
	}

	public void SetBestTime(int bestTime) {
		this.bestTime = bestTime;
		this.bestTimeDate = new Date();
	}

	public GameStatistics(int gameSize) {
		this.gameSize = gameSize;
		this.gamesPlayed = 0;
		this.gamesWon = 0;
		this.totalSeconds = 0;
		this.bestTime = Integer.MAX_VALUE;
		this.bestTimeDate = null;
	}

	// #region JSON Serialization

	private final static String GameSize = "GameSize";
	private final static String GamesPlayed = "GamesPlayed";
	private final static String GamesWon = "GamesWon";
	private final static String TotalSeconds = "TotalSeconds";
	private final static String BestTime = "BestTime";
	private final static String BestTimeDate = "BestTimeDate";

	public JSONObject ToJson() {
		JSONObject json = new JSONObject();
		try {
			json.put(GameStatistics.GameSize, this.gameSize);
			json.put(GameStatistics.GamesPlayed, this.gamesPlayed);
			json.put(GameStatistics.GamesWon, this.gamesWon);
			json.put(GameStatistics.TotalSeconds, this.totalSeconds);

			if (this.bestTimeDate != null) {
				json.put(GameStatistics.BestTime, this.bestTime);
				json.put(
					GameStatistics.BestTimeDate,
					GameStatistics.dateFormatter.format(
						this.bestTimeDate
					)
				);
			}

		} catch(JSONException e) {
			// Should never except here
			e.printStackTrace();
		}

		return json;
	}

	public GameStatistics(JSONObject json) {

		try {
			this.gameSize = json.getInt(GameStatistics.GameSize);
			this.gamesPlayed = json.getInt(GameStatistics.GamesPlayed);
			this.gamesWon = json.getInt(GameStatistics.GamesWon);
			this.totalSeconds = json.getInt(GameStatistics.TotalSeconds);

			if (json.has(GameStatistics.BestTimeDate)) {
				this.bestTime = json.getInt(GameStatistics.BestTime);
				this.bestTimeDate =
					GameStatistics.dateFormatter.parse(
						json.getString(GameStatistics.BestTimeDate)
					);
			}

		} catch(JSONException e) {
			// Big problem...
			e.printStackTrace();
		} catch (ParseException e) {
			// This catches the parse exception
			// Though this should never occur since the only time
			//  that the stuff is ever written is by the formatter
			//  itself.
			e.printStackTrace();
		}
	}

	// #endregion
}