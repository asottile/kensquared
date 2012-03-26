package com.anthonysottile.kenken;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;

public class RenderLine {

	private Point position;
	public Point getPosition() {
		return this.position;
	}
	
	private int length;
	public int getLength() {
		return this.length;
	}
	
	private boolean horizontal;
	public boolean getHorizontal() {
		return this.horizontal;
	}
	
    public RenderLine(Point position, int length, boolean horizontal)
    {
        this.position = position;
        this.length = length;
        this.horizontal = horizontal;
    }

	// #region JSON Serialization
	
	private static final String positionProperty = "Position";
	private static final String lengthProperty = "Length";
	private static final String horizontalProperty = "Horizontal";
	
	public RenderLine(JSONObject json) {
		
		try {
			this.position = Points.ToPoint(json.getJSONObject(positionProperty));
			this.length = json.getInt(lengthProperty);
			this.horizontal = json.getBoolean(horizontalProperty);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JSONObject ToJson() {
		JSONObject json = new JSONObject();
		
		try {
			json.put(positionProperty, Points.ToJson(this.position));
			json.put(lengthProperty, this.length);
			json.put(horizontalProperty, this.horizontal);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	// #endregion
    
}
