package com.anthonysottile.kenken;

import android.graphics.Point;

import org.json.JSONException;
import org.json.JSONObject;

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

    public RenderLine(Point position, int length, boolean horizontal) {
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
            this.position = Points.INSTANCE.toPoint(json.getJSONObject(RenderLine.positionProperty));
            this.length = json.getInt(RenderLine.lengthProperty);
            this.horizontal = json.getBoolean(RenderLine.horizontalProperty);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject ToJson() {
        JSONObject json = new JSONObject();

        try {
            json.put(RenderLine.positionProperty, Points.INSTANCE.toJson(this.position));
            json.put(RenderLine.lengthProperty, this.length);
            json.put(RenderLine.horizontalProperty, this.horizontal);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    // #endregion

}
