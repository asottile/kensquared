package com.anthonysottile.kenken.cages;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;
import com.anthonysottile.kenken.UserSquare;

import android.graphics.Point;

public abstract class BaseCage implements ICage {

	protected SignNumber signNumber;
	protected List<RenderLine> renderLines = new ArrayList<RenderLine>();
	protected List<Point> squares = new ArrayList<Point>();
	protected Point signLocation;
	
	public SignNumber getSignNumber() {
		return this.signNumber;
	}

	public List<RenderLine> getRenderLines() {
		return this.renderLines;
	}
	
	public List<Point> getSquares() {
		return this.squares;
	}

	public Point getSignLocation() {
		return this.signLocation;
	}
	
	public boolean cageIsValid(UserSquare[][] userSquares) {
		
		int squaresSize = this.squares.size();
		int[] values = new int[squaresSize];
		for (int i = 0; i < squaresSize; i += 1) {
			Point point = this.squares.get(i);
			UserSquare square = userSquares[point.x][point.y];
			int value = square.getValue();
			if (value == 0) {
				return false;
			}
			values[i] = value;
		}
		
		int number = this.signNumber.getNumber();
		
		// Got to here, need to validate.
		switch(this.signNumber.getSign()) {
			case Add:
				return CageGenerator.sum(values) == number;
			case Subtract:
				return CageGenerator.max(values) - CageGenerator.min(values) == number;
			case Multiply:
				return CageGenerator.product(values) == number;
			case Divide:
				return CageGenerator.max(values) / CageGenerator.min(values) == number;
			case None:
			default:
				return values[0] == number;
		}
	}
	
	// #region JSON Serialization
	
	private static final String signNumberProperty = "SignNumber";
	private static final String renderLinesProperty = "RenderLines";
	private static final String squaresProperty = "Squares";
	private static final String signLocationProperty  = "SignLocation";
	
	public JSONObject ToJson() {
		
		JSONObject json = new JSONObject();

		try {			
			JSONArray renderLinesJson = new JSONArray();
			int size = this.renderLines.size();
			for (int i = 0; i < size; i += 1) {
				renderLinesJson.put(i, this.renderLines.get(i).ToJson());
			}
			
			JSONArray squaresJson = new JSONArray();
			int squaresSize = this.squares.size();
			for (int i = 0; i < squaresSize; i += 1) {
				squaresJson.put(i, Points.ToJson(this.squares.get(i)));
			}
			
			json.put(signNumberProperty, this.signNumber.ToJson());
			json.put(renderLinesProperty, renderLinesJson);
			json.put(squaresProperty, squaresJson);
			json.put(signLocationProperty, Points.ToJson(this.signLocation));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}

	private static class JsonCage extends BaseCage {
		
		public JsonCage(JSONObject json) {
			
			try {
				JSONArray renderLinesJson = json.getJSONArray(renderLinesProperty);
				
				int length = renderLinesJson.length();
				for (int i = 0; i < length; i += 1) {
					RenderLine line =
						new RenderLine(
							renderLinesJson.getJSONObject(i)
						);
					
					this.renderLines.add(line);
				}
				
				JSONArray squaresJson = json.getJSONArray(squaresProperty);
				
				int squaresLength = squaresJson.length();
				for (int i = 0; i < squaresLength; i += 1) {
					Point point =
						Points.ToPoint(squaresJson.getJSONObject(i));
					this.squares.add(point);
				}
				
				this.signNumber =
					new SignNumber(
						json.getJSONObject(signNumberProperty)
					);
				this.signLocation =
					Points.ToPoint(
						json.getJSONObject(signLocationProperty)
					);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static ICage ToCage(JSONObject json) {
		return new JsonCage(json);
	}
	
	// #endregion
	
	protected BaseCage() { }
}
