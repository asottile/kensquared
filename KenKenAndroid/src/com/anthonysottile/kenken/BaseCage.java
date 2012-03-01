package com.anthonysottile.kenken;

import java.util.List;

import android.graphics.Point;

public abstract class BaseCage implements ICage {

	protected SignNumber signNumber;
	protected List<RenderLine> renderLines;
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
