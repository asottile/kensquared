package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;

import com.anthonysottile.kenken.UserSquare.ValueSetEvent;
import com.anthonysottile.kenken.cages.BaseCage;
import com.anthonysottile.kenken.cages.CageGenerator;
import com.anthonysottile.kenken.cages.ICage;

public class KenKenGame {

	// Update this as squares obtain values
	private int squaresWithValues = 0;
	public int getSquaresWithValues() {
		return this.squaresWithValues;
	}

	private Date gameStartTime;
	public Date getGameStartTime() {
		return this.gameStartTime;
	}

	/**
	 * Resets the game time to the specified milliseconds.
	 * @param milliseconds The amount of elapsed time to set the game time
	 *                      to.
	 */
	public void ResetGameStartTime(long milliseconds) {
		Date date = new Date();
		date.setTime(date.getTime() - milliseconds);
		this.gameStartTime = date;
	}

	public void PenalizeGameStartTime(long milliseconds) {
		this.gameStartTime.setTime(this.gameStartTime.getTime() - milliseconds);
	}

	private LatinSquare latinSquare;
	public LatinSquare getLatinSquare() {
		return this.latinSquare;
	}

	private boolean[][] cageSquareOccupied;

	private List<ICage> cages;
	public List<ICage> getCages() {
		return this.cages;
	}

	private UserSquare[][] userSquares;
	public UserSquare[][] getUserSquares() {
		return this.userSquares;
	}

	public boolean squareIsOffBoard(Point p) {
		int order = this.latinSquare.getOrder();

		return
				p.x >= order ||
				p.y >= order ||
				p.x < 0 ||
				p.y < 0;
	}

	public boolean squareIsValid(Point p) {
		if (this.squareIsOffBoard(p)) {
			return false;
		}

		return !this.cageSquareOccupied[p.x][p.y];
	}

	public void setOccupied(Point p) {
		this.cageSquareOccupied[p.x][p.y] = true;
	}

	private final UserSquare.ValueSetListener valueSetListener =
		new UserSquare.ValueSetListener() {
			public void onValueSet(ValueSetEvent event) {
				if (event.getValue() > 0) {
					KenKenGame.this.squaresWithValues += 1;
				} else {
					KenKenGame.this.squaresWithValues -= 1;
				}
			}
		};

	private void postInitialize() {
		// For shared "constructor" code

		// We are going to attach to the value set event on our user squares to
		//  make sure they have a value when being selected.  This way we can count
		//  the number of squares the user has filled in and allow for a faster
		//  calculation of the winning condition.

		int order = this.latinSquare.getOrder();

		for (int i = 0; i < order; i += 1) {
			for (int j = 0; j < order; j += 1) {
				this.userSquares[i][j].AddValueSetListener(this.valueSetListener);
			}
		}
	}

	public KenKenGame(int order) {
		this.latinSquare = new LatinSquare(order);

		this.cageSquareOccupied = new boolean[order][];
		this.userSquares = new UserSquare[order][];
		for (int i = 0; i < order; i += 1) {
			this.cageSquareOccupied[i] = new boolean[order];
			this.userSquares[i] = new UserSquare[order];

			for (int j = 0; j < order; j += 1) {
				this.cageSquareOccupied[i][j] = false;
				this.userSquares[i][j] = new UserSquare(i, j, order);
			}
		}

		this.cages = new ArrayList<ICage>();

		CageGenerator.Generate(this);

		this.gameStartTime = new Date();

		this.postInitialize();
	}

	// #region JSON Serialization

	private static final String squaresWithValuesProperty = "SquaresWithValues";
	private static final String gameTimeElapsedProperty = "GameTimeElapsed";
	private static final String latinSquareProperty = "LatinSquare";
	private static final String cagesProperty = "Cages";
	private static final String userSquaresProperty = "UserSquares";

	public JSONObject ToJson() {
		JSONObject json = new JSONObject();

		try {

			Date now = new Date();
			long timeElapsed = now.getTime() - this.gameStartTime.getTime();

			JSONArray cagesJson = new JSONArray();
			int cagesLength = this.cages.size();
			for (int i = 0; i < cagesLength; i += 1) {
				cagesJson.put(i, this.cages.get(i).ToJson());
			}

			JSONArray userSquaresJson = new JSONArray();
			for (int i = 0; i < this.userSquares.length; i += 1) {

				JSONArray innerArray = new JSONArray();

				for (int j = 0; j < this.userSquares[i].length; j += 1) {
					innerArray.put(j, this.userSquares[i][j].ToJson());
				}

				userSquaresJson.put(i, innerArray);
			}

			json.put(KenKenGame.squaresWithValuesProperty, this.squaresWithValues);
			json.put(KenKenGame.gameTimeElapsedProperty, timeElapsed);
			json.put(KenKenGame.latinSquareProperty, this.latinSquare.ToJson());
			json.put(KenKenGame.cagesProperty, cagesJson);
			json.put(KenKenGame.userSquaresProperty, userSquaresJson);

		} catch(JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public KenKenGame(JSONObject json) {
		try {

			this.squaresWithValues = json.getInt(KenKenGame.squaresWithValuesProperty);

			long timeElapsed = json.getLong(KenKenGame.gameTimeElapsedProperty);
			this.gameStartTime = new Date();
			this.gameStartTime.setTime(this.gameStartTime.getTime() - timeElapsed);

			this.latinSquare =	new LatinSquare(json.getJSONObject(KenKenGame.latinSquareProperty));

			JSONArray cagesJson = json.getJSONArray(KenKenGame.cagesProperty);
			this.cages = new ArrayList<ICage>();
			int cagesSize = cagesJson.length();
			for (int i = 0; i < cagesSize; i += 1) {
				this.cages.add(BaseCage.ToCage(cagesJson.getJSONObject(i)));
			}

			JSONArray userSquareJson = json.getJSONArray(KenKenGame.userSquaresProperty);
			this.userSquares = new UserSquare[userSquareJson.length()][];
			for (int i = 0; i < this.userSquares.length; i += 1) {

				this.userSquares[i] = new UserSquare[this.userSquares.length];
				JSONArray innerArray = userSquareJson.getJSONArray(i);

				for (int j = 0; j < this.userSquares[i].length; j += 1) {
					this.userSquares[i][j] =
						new UserSquare(
							innerArray.getJSONObject(j)
						);
				}
			}

			this.postInitialize();

		} catch(JSONException e) {
			e.printStackTrace();
		}
	}

	// #endregion
}
