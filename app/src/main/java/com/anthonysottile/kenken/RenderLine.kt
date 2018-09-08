package com.anthonysottile.kenken

import android.graphics.Point
import org.json.JSONObject

class RenderLine {
    val position: Point
    val length: Int
    val horizontal: Boolean

    constructor(position: Point, length: Int, horizontal: Boolean) {
        this.position = position
        this.length = length
        this.horizontal = horizontal
    }

    constructor(json: JSONObject) {
        this.position = Points.toPoint(json.getJSONObject(RenderLine.positionProperty))
        this.length = json.getInt(RenderLine.lengthProperty)
        this.horizontal = json.getBoolean(RenderLine.horizontalProperty)
    }

    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put(RenderLine.positionProperty, Points.toJson(this.position))
        json.put(RenderLine.lengthProperty, this.length)
        json.put(RenderLine.horizontalProperty, this.horizontal)

        return json
    }

    companion object {
        private const val positionProperty = "Position"
        private const val lengthProperty = "Length"
        private const val horizontalProperty = "Horizontal"
    }
}
