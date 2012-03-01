package com.anthonysottile.kenken;

import android.graphics.Point;

public final class Points {

	public static final Point Up = new Point(0, -1);
	public static final Point Down = new Point(0, 1);
	public static final Point Left = new Point(-1, 0);
	public static final Point Right = new Point(1, 0);
	
	public static Point add(Point lhs, Point rhs) {
		Point newPoint = new Point(lhs.x + rhs.x, lhs.y + rhs.y);
		return newPoint;
	}
	
	private Points() {}

	public static Point multiply(int multiplier, Point p) {
		Point newPoint = new Point(p.x * multiplier, p.y * multiplier);
		return newPoint;
	}
}
