package com.anthonysottile.kenken;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;

public final class Points {

	public static final Point Up = new Point(0, -1);
	public static final Point Down = new Point(0, 1);
	public static final Point Left = new Point(-1, 0);
	public static final Point Right = new Point(1, 0);
		
	public static Point add(Point lhs, Point rhs) {
		Point newPoint = new Point(lhs.x + rhs.x, lhs.y + rhs.y);
		return newPoint;
	}

	public static Point multiply(int multiplier, Point p) {
		Point newPoint = new Point(p.x * multiplier, p.y * multiplier);
		return newPoint;
	}
	
	// #region JSON serialization

	private static final String xProperty = "X";
	private static final String yProperty = "Y";
	
	public static JSONObject ToJson(Point p) {
		JSONObject json = new JSONObject();
		
		try {
			json.put(Points.xProperty, p.x);
			json.put(Points.yProperty, p.y);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public static Point ToPoint(JSONObject json) {
		
		int x = 0;
		int y = 0;
		try {
			x = json.getInt(Points.xProperty);
			y = json.getInt(Points.yProperty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new Point(x, y);
	}
	
	// #endregion
	
	private Points() {}
}
