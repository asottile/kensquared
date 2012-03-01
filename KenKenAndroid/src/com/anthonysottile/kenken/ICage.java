package com.anthonysottile.kenken;

import java.util.List;

import android.graphics.Point;

public interface ICage {

	public SignNumber getSignNumber();
	
	public List<RenderLine> getRenderLines();
	
	public Point getSignLocation();
}
