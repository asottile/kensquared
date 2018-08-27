package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;
import com.anthonysottile.kenken.UserSquare;

import org.json.JSONObject;

import java.util.List;

public interface ICage {

    SignNumber getSignNumber();

    List<RenderLine> getRenderLines();

    List<Point> getSquares();

    Point getSignLocation();

    boolean cageIsValid(UserSquare[][] userSquares);

    JSONObject ToJson();
}
