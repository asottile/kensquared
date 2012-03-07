package com.anthonysottile.kenken.cages;

import java.util.ArrayList;
import java.util.List;

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
	
	protected BaseCage() {
	}
}
