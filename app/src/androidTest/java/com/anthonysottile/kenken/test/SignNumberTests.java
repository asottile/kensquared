package com.anthonysottile.kenken.test;

import com.anthonysottile.kenken.Sign;
import com.anthonysottile.kenken.SignNumber;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

public class SignNumberTests extends TestCase {

    public void testGetSign() {
        final Sign signValue = Sign.Add;
        final int signNumber = 15;
        SignNumber test = new SignNumber(signValue, signNumber);
        assertEquals(test.getSign(), signValue);
    }

    public void testGetNumber() {
        final Sign signValue = Sign.Add;
        final int signNumber = 15;
        SignNumber test = new SignNumber(signValue, signNumber);
        assertEquals(test.getNumber(), signNumber);
    }

    public void testToString() {
        final Sign signValue = Sign.Add;
        final int signNumber = 15;
        SignNumber test = new SignNumber(signValue, signNumber);
        assertEquals(test.toString(), "15 +");
    }

    public void testSignNumberSignInt() {
        final Sign signValue = Sign.Add;
        final int signNumber = 15;
        SignNumber test = new SignNumber(signValue, signNumber);
        assertEquals(test.getSign(), signValue);
        assertEquals(test.getNumber(), signNumber);
    }

    public void testSignNumberJSONObject() {
        JSONObject json = new JSONObject();

        try {
            json.put("Sign", Sign.Add.getIntValue());
            json.put("Number", 15);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("Json object through exception");
        }

        SignNumber test = new SignNumber(json);
        assertEquals(test.getSign(), Sign.Add);
        assertEquals(test.getNumber(), 15);
    }

    public void testToJson() {
        SignNumber test = new SignNumber(Sign.Add, 15);

        JSONObject json = test.toJson();

        try {
            assertEquals(json.getInt("Sign"), Sign.Add.getIntValue());
            assertEquals(json.getInt("Number"), 15);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("Json object through exception");
        }
    }
}
