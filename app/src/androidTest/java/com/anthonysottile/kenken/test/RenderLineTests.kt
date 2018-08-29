package com.anthonysottile.kenken.test

import android.graphics.Point

import com.anthonysottile.kenken.RenderLine

import junit.framework.TestCase

import org.json.JSONException
import org.json.JSONObject

class RenderLineTests : TestCase() {

    fun testGetPosition() {
        val line = RenderLine(point, length, horizontal)
        assertEquals(line.position.x, pointX)
        assertEquals(line.position.y, pointY)
    }

    fun testGetLength() {
        val line = RenderLine(point, length, horizontal)
        assertEquals(line.length, length)
    }

    fun testGetHorizontal() {
        val line = RenderLine(point, length, horizontal)
        assertEquals(line.horizontal, horizontal)
    }

    fun testRenderLinePointIntBoolean() {
        val line = RenderLine(point, length, horizontal)
        assertEquals(line.position.x, pointX)
        assertEquals(line.position.y, pointY)
        assertEquals(line.length, length)
        assertEquals(line.horizontal, horizontal)
    }

    fun testRenderLineJSONObject() {

        val json = JSONObject()
        val pos = JSONObject()
        pos.put("X", pointX)
        pos.put("Y", pointY)

        json.put("Position", pos)
        json.put("Length", length)
        json.put("Horizontal", horizontal)

        val line = RenderLine(json)
        assertEquals(line.position.x, pointX)
        assertEquals(line.position.y, pointY)
        assertEquals(line.length, length)
        assertEquals(line.horizontal, horizontal)
    }

    fun testToJson() {
        val line = RenderLine(point, length, horizontal)
        val json = line.toJson()

        val pos = json.getJSONObject("Position")
        assertEquals(pos.getInt("X"), pointX)
        assertEquals(pos.getInt("Y"), pointY)

        assertEquals(json.getInt("Length"), length)
        assertEquals(json.getBoolean("Horizontal"), horizontal)
    }

    companion object {
        private const val pointX = 9
        private const val pointY = 15
        private val point = Point(pointX, pointY)
        private const val length = 15
        private const val horizontal = false
    }
}
