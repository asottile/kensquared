package com.anthonysottile.kenken;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;

public final class Points {

	/**
	 * Point representing one unit up on a 0, 0 upper left coordinate plane.
	 */
	public static final Point Up = new Point(0, -1);

	/**
	 * Point representing one unit down on a 0, 0 upper left coordinate plane.
	 */
	public static final Point Down = new Point(0, 1);

	/**
	 * Point representing one unit left on a 0, 0 upper left coordinate plane.
	 */
	public static final Point Left = new Point(-1, 0);

	/**
	 * Point representing one unit right on a 0, 0 upper left coordinate plane.
	 */
	public static final Point Right = new Point(1, 0);
		
	/**
	 * Adds the two points and returns a new point.
	 * 
	 * @param lhs The left hand point to add.
	 * @param rhs The right hand point to add.
	 * @return The sum of the two points.
	 */
	public static Point add(Point lhs, Point rhs) {
		Point newPoint = new Point(lhs.x + rhs.x, lhs.y + rhs.y);
		return newPoint;
	}

	/**
	 * Multiplies a point by a scalar and returns a new point.
	 * 
	 * @param multiplier The scalar to multiply the point by.
	 * @param p The point to multiply by the scalar.
	 * @return A new point which is the result of the point multiplied by the scalar.
	 */
	public static Point multiply(int multiplier, Point p) {
		Point newPoint = new Point(p.x * multiplier, p.y * multiplier);
		return newPoint;
	}
	
	// #region JSON serialization

	private static final String xProperty = "X";
	private static final String yProperty = "Y";
	
	/**
	 * Serializes a point to a JSONObject
	 * 
	 * @param p Point to serialize to JSON.
	 * @return Returns the point as a JSONObject.
	 */
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
	
	/**
	 * De-serializes a Point from JSON
	 * 
	 * @param json The JSONObject of the point.
	 * @return Returns the point from a JSONObject.
	 */
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
