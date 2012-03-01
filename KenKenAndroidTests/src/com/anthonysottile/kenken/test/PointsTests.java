package com.anthonysottile.kenken.test;

import android.graphics.Point;

import com.anthonysottile.kenken.Points;

import junit.framework.TestCase;

public class PointsTests extends TestCase {

	private final int startPointX = 1;
	private final int startPointY = 2;
	private final Point point = new Point(startPointX, startPointY);

	private final int startPoint2X = 4;
	private final int startPoint2Y = 8;
	private final Point point2 = new Point(startPoint2X, startPoint2Y);

	private final int startPoint3X = -5;
	private final int startPoint3Y = -7;
	private final Point point3 = new Point(startPoint3X, startPoint3Y);
	
	public void testAddPositive() {
		// Get result point and assert its value
		Point resultPoint = Points.add(point, point2);
		assertEquals(startPointX + startPoint2X, resultPoint.x);
		assertEquals(startPointY + startPoint2Y, resultPoint.y);
		
		// Make sure original points were un altered
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
		assertEquals(startPoint2X, point2.x);
		assertEquals(startPoint2Y, point2.y);
	}
	
	public void testAddNegative() {
		// Get result point and assert its value
		Point resultPoint2 = Points.add(point, point3);
		assertEquals(startPointX + startPoint3X, resultPoint2.x);
		assertEquals(startPointY + startPoint3Y, resultPoint2.y);
		
		// Make sure the originals were not touched
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
		assertEquals(startPoint3X, point3.x);
		assertEquals(startPoint3Y, point3.y);
	}

	public void testMultiplyIdentity() {
		// Multiply and check
		Point resultPoint3 = Points.multiply(1, point);
		assertEquals(startPointX, resultPoint3.x);
		assertEquals(startPointY, resultPoint3.y);

		// Make sure original points were not altered
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
	}
	
	public void testMultiplyNegative() {
		// Multiply and check
		Point resultPoint2 = Points.multiply(-1, point);
		assertEquals(-1 * startPointX, resultPoint2.x);
		assertEquals(-1 * startPointY, resultPoint2.y);

		// Make sure original points were not altered
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
	}
	
	public void testMultiplyZero() {
		// Multiply and check
		Point resultPoint4 = Points.multiply(0, point);
		assertEquals(0 * startPointX, resultPoint4.x);
		assertEquals(0 * startPointY, resultPoint4.y);

		// Make sure original points were not altered
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
	}
	
	public void testMultiplyScalar() {
		// Multiply and check
		Point resultPoint5 = Points.multiply(2, point);
		assertEquals(2 * startPointX, resultPoint5.x);
		assertEquals(2 * startPointY, resultPoint5.y);

		// Make sure original points were not altered
		assertEquals(startPointX, point.x);
		assertEquals(startPointY, point.y);
	}
}
