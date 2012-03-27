package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anthonysottile.kenken.cages.BaseCage;
import com.anthonysottile.kenken.cages.CageGenerator;
import com.anthonysottile.kenken.cages.ICage;

import android.graphics.Point;

public class KenKenGame {
	
	private Date gameStartTime;
	public Date getGameStartTime() {
		return this.gameStartTime;
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
		if(this.squareIsOffBoard(p)) {
			return false;
		}
		
		return !this.cageSquareOccupied[p.x][p.y];
	}

	public void setOccupied(Point p) {
		this.cageSquareOccupied[p.x][p.y] = true; 
	}
	
	public KenKenGame(int order) {
		this.latinSquare = new LatinSquare(order);
		
		this.cageSquareOccupied = new boolean[order][];
		this.userSquares = new UserSquare[order][];
		for(int i = 0; i < order; i += 1) {
			this.cageSquareOccupied[i] = new boolean[order];
			this.userSquares[i] = new UserSquare[order];
			
			for(int j = 0; j < order; j += 1) {
				this.cageSquareOccupied[i][j] = false;
				this.userSquares[i][j] = new UserSquare(i, j, order);
			}
		}
		
		this.cages = new ArrayList<ICage>();

		CageGenerator.Generate(this);
		
		this.gameStartTime = new Date();
	}
	
	// #region JSON Serialization
	
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
			for(int i = 0; i < cagesLength; i += 1) {
				cagesJson.put(i, this.cages.get(i).ToJson());
			}
			
			JSONArray userSquaresJson = new JSONArray();
			for(int i = 0; i < this.userSquares.length; i += 1) {
				
				JSONArray innerArray = new JSONArray();
				
				for(int j = 0; j < this.userSquares[i].length; j += 1) {
					innerArray.put(j, this.userSquares[i][j].ToJson());
				}
				
				userSquaresJson.put(i, innerArray);
			}
			
			json.put(gameTimeElapsedProperty, timeElapsed);
			json.put(latinSquareProperty, this.latinSquare.ToJson());
			json.put(cagesProperty, cagesJson);
			json.put(userSquaresProperty, userSquaresJson);
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public KenKenGame(JSONObject json) {
		try {
			
			long timeElapsed = json.getLong(gameTimeElapsedProperty);
			this.gameStartTime = new Date();
			this.gameStartTime.setTime(this.gameStartTime.getTime() - timeElapsed);
			
			this.latinSquare =
				new LatinSquare(
					json.getJSONObject(latinSquareProperty)
				);
			
			JSONArray cagesJson = json.getJSONArray(cagesProperty);
			this.cages = new ArrayList<ICage>();
			int cagesSize = cagesJson.length();
			for(int i = 0; i < cagesSize; i += 1) {
				this.cages.add(BaseCage.ToCage(cagesJson.getJSONObject(i)));
			}
			
			JSONArray userSquareJson = json.getJSONArray(userSquaresProperty);
			this.userSquares = new UserSquare[userSquareJson.length()][];
			for(int i = 0; i < this.userSquares.length; i += 1) {
				
				this.userSquares[i] = new UserSquare[this.userSquares.length];
				JSONArray innerArray = userSquareJson.getJSONArray(i);
		
				for(int j = 0; j < this.userSquares[i].length; j += 1) {
					this.userSquares[i][j] =
						new UserSquare(
							innerArray.getJSONObject(j)
						);
				}
			}
			
		} catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	// #endregion
}
