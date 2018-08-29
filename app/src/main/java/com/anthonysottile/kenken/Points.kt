package com.anthonysottile.kenken

import android.graphics.Point
import org.json.JSONObject

object Points {

    /**
     * Point representing one unit down on a 0, 0 upper left coordinate plane.
     */
    val Down = Point(0, 1)

    /**
     * Point representing one unit left on a 0, 0 upper left coordinate plane.
     */
    val Left = Point(-1, 0)

    /**
     * Point representing one unit right on a 0, 0 upper left coordinate plane.
     */
    val Right = Point(1, 0)

    private const val xProperty = "X"
    private const val yProperty = "Y"

    /**
     * Adds the two points and returns a new point.
     *
     * @param lhs The left hand point to add.
     * @param rhs The right hand point to add.
     * @return The sum of the two points.
     */
    fun add(lhs: Point, rhs: Point): Point {
        return Point(lhs.x + rhs.x, lhs.y + rhs.y)
    }

    /**
     * Multiplies a point by a scalar and returns a new point.
     *
     * @param multiplier The scalar to multiply the point by.
     * @param p          The point to multiply by the scalar.
     * @return A new point which is the result of the point multiplied by the scalar.
     */
    fun multiply(multiplier: Int, p: Point): Point {
        return Point(p.x * multiplier, p.y * multiplier)
    }

    fun toJson(p: Point): JSONObject {
        val json = JSONObject()

        json.put(Points.xProperty, p.x)
        json.put(Points.yProperty, p.y)

        return json
    }

    fun toPoint(json: JSONObject): Point {
        return Point(
                json.getInt(Points.xProperty),
                json.getInt(Points.yProperty)
        )
    }
}
