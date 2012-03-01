package com.anthonysottile.kenken;

import android.graphics.Point;

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
	
    public RenderLine(Point position, int length, boolean horizontal)
    {
        this.position = position;
        this.length = length;
        this.horizontal = horizontal;
    }
}
