package com.anthonysottile.kenken.cages;

import java.util.List;

import org.json.JSONObject;

import android.graphics.Point;

import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;
import com.anthonysottile.kenken.UserSquare;

public interface ICage {

	public SignNumber getSignNumber();

	public List<RenderLine> getRenderLines();

	public List<Point> getSquares();

	public Point getSignLocation();

	public boolean cageIsValid(UserSquare[][] userSquares);

	public JSONObject ToJson();
}
