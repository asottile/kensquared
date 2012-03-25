package com.anthonysottile.kenken.cages;

import java.util.List;

import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SignNumber;

import android.graphics.Point;

public interface ICage {

	public SignNumber getSignNumber();
	
	public List<RenderLine> getRenderLines();
	
	public Point getSignLocation();
}