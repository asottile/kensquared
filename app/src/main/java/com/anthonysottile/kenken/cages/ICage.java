package com.anthonysottile.kenken.cages;

import java.util.List;

import org.json.JSONObject;

import android.graphics.Point;

import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;
import com.anthonysottile.kenken.UserSquare;

public interface ICage {

	SignNumber getSignNumber();

	List<RenderLine> getRenderLines();

	List<Point> getSquares();

	Point getSignLocation();

	boolean cageIsValid(UserSquare[][] userSquares);

	JSONObject ToJson();
}
