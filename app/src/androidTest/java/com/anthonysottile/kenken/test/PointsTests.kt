package com.anthonysottile.kenken.test

import android.graphics.Point
import com.anthonysottile.kenken.Points
import junit.framework.TestCase
import org.json.JSONObject

class PointsTests : TestCase() {

    private val startPointX = 1
    private val startPointY = 2
    private val point = Point(startPointX, startPointY)

    private val startPoint2X = 4
    private val startPoint2Y = 8
    private val point2 = Point(startPoint2X, startPoint2Y)

    private val startPoint3X = -5
    private val startPoint3Y = -7
    private val point3 = Point(startPoint3X, startPoint3Y)

    fun testAddPositive() {
        // Get result point and assert its value
        val resultPoint = Points.add(point, point2)
        assertEquals(startPointX + startPoint2X, resultPoint.x)
        assertEquals(startPointY + startPoint2Y, resultPoint.y)

        // Make sure original points were un altered
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
        assertEquals(startPoint2X, point2.x)
        assertEquals(startPoint2Y, point2.y)
    }

    fun testAddNegative() {
        // Get result point and assert its value
        val resultPoint2 = Points.add(point, point3)
        assertEquals(startPointX + startPoint3X, resultPoint2.x)
        assertEquals(startPointY + startPoint3Y, resultPoint2.y)

        // Make sure the originals were not touched
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
        assertEquals(startPoint3X, point3.x)
        assertEquals(startPoint3Y, point3.y)
    }

    fun testMultiplyIdentity() {
        // Multiply and check
        val resultPoint3 = Points.multiply(1, point)
        assertEquals(startPointX, resultPoint3.x)
        assertEquals(startPointY, resultPoint3.y)

        // Make sure original points were not altered
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
    }

    fun testMultiplyNegative() {
        // Multiply and check
        val resultPoint2 = Points.multiply(-1, point)
        assertEquals(-1 * startPointX, resultPoint2.x)
        assertEquals(-1 * startPointY, resultPoint2.y)

        // Make sure original points were not altered
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
    }

    fun testMultiplyZero() {
        // Multiply and check
        val resultPoint4 = Points.multiply(0, point)
        assertEquals(0, resultPoint4.x)
        assertEquals(0, resultPoint4.y)

        // Make sure original points were not altered
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
    }

    fun testMultiplyScalar() {
        // Multiply and check
        val resultPoint5 = Points.multiply(2, point)
        assertEquals(2 * startPointX, resultPoint5.x)
        assertEquals(2 * startPointY, resultPoint5.y)

        // Make sure original points were not altered
        assertEquals(startPointX, point.x)
        assertEquals(startPointY, point.y)
    }

    fun testToJSON() {
        val json = Points.toJson(point)

        assertEquals(json.getInt("X"), startPointX)
        assertEquals(json.getInt("Y"), startPointY)
    }

    fun testToPoint() {
        val json = JSONObject()

        json.put("X", startPointX)
        json.put("Y", startPointY)

        val p = Points.toPoint(json)
        assertEquals(p.x, startPointX)
        assertEquals(p.y, startPointY)
    }
}
