package com.anthonysottile.kenken.test;

import android.graphics.Point;

import com.anthonysottile.kenken.RenderLine;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

public class RenderLineTests extends TestCase {

    private static final int pointX = 9;
    private static final int pointY = 15;
    private static final Point point = new Point(pointX, pointY);
    private static final int length = 15;
    private static final boolean horizontal = false;

    public void testGetPosition() {
        RenderLine line = new RenderLine(point, length, horizontal);
        assertEquals(line.getPosition().x, pointX);
        assertEquals(line.getPosition().y, pointY);
    }

    public void testGetLength() {
        RenderLine line = new RenderLine(point, length, horizontal);
        assertEquals(line.getLength(), length);
    }

    public void testGetHorizontal() {
        RenderLine line = new RenderLine(point, length, horizontal);
        assertEquals(line.getHorizontal(), horizontal);
    }

    public void testRenderLinePointIntBoolean() {
        RenderLine line = new RenderLine(point, length, horizontal);
        assertEquals(line.getPosition().x, pointX);
        assertEquals(line.getPosition().y, pointY);
        assertEquals(line.getLength(), length);
        assertEquals(line.getHorizontal(), horizontal);
    }

    public void testRenderLineJSONObject() {

        JSONObject json = new JSONObject();
        try {
            JSONObject pos = new JSONObject();
            pos.put("X", pointX);
            pos.put("Y", pointY);

            json.put("Position", pos);
            json.put("Length", length);
            json.put("Horizontal", horizontal);
        } catch (JSONException e) {
            fail("JSON Failed");
        }

        RenderLine line = new RenderLine(json);
        assertEquals(line.getPosition().x, pointX);
        assertEquals(line.getPosition().y, pointY);
        assertEquals(line.getLength(), length);
        assertEquals(line.getHorizontal(), horizontal);
    }

    public void testToJson() {
        RenderLine line = new RenderLine(point, length, horizontal);
        JSONObject json = line.ToJson();

        try {
            JSONObject pos = json.getJSONObject("Position");
            assertEquals(pos.getInt("X"), pointX);
            assertEquals(pos.getInt("Y"), pointY);

            assertEquals(json.getInt("Length"), length);
            assertEquals(json.getBoolean("Horizontal"), horizontal);
        } catch (JSONException e) {
            fail("JSON Failed");
        }
    }
}
