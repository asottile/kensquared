package com.anthonysottile.kenken.cages;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;

import android.graphics.Point;

public abstract class BaseCage implements ICage {

	protected SignNumber signNumber;
	protected List<RenderLine> renderLines = new ArrayList<RenderLine>();
	protected Point signLocation;
	
	public SignNumber getSignNumber() {
		return this.signNumber;
	}

	public List<RenderLine> getRenderLines() {
		return this.renderLines;
	}

	public Point getSignLocation() {
		return this.signLocation;
	}
	
	// #region JSON Serialization
	
	private static final String signNumberProperty = "SignNumber";
	private static final String renderLinesProperty = "RenderLines";
	private static final String signLocationProperty  = "SignLocation";
	
	public JSONObject ToJson() {
		
		JSONObject json = new JSONObject();

		try {			
			JSONArray renderLinesJson = new JSONArray();
			int size = this.renderLines.size();
			for(int i = 0; i < size; i += 1) {
				renderLinesJson.put(i, this.renderLines.get(i).ToJson());
			}
			
			json.put(signNumberProperty, this.signNumber.ToJson());
			json.put(renderLinesProperty, renderLinesJson);
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
				for(int i = 0; i < length; i += 1) {
					RenderLine line =
						new RenderLine(
							renderLinesJson.getJSONObject(i)
						);
					
					this.renderLines.add(line);
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
