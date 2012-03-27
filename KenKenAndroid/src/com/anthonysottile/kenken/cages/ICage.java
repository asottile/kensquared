package com.anthonysottile.kenken.cages;

import java.util.List;

import org.json.JSONObject;

import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;
import com.anthonysottile.kenken.UserSquare;

import android.graphics.Point;

public interface ICage {

	public SignNumber getSignNumber();
	
	public List<RenderLine> getRenderLines();
	
	public List<Point> getSquares();
	
	public Point getSignLocation();
	
	public boolean cageIsValid(UserSquare[][] userSquares);
	
	public JSONObject ToJson();
}
