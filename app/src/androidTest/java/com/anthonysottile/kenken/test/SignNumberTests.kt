package com.anthonysottile.kenken.test

import com.anthonysottile.kenken.Sign
import com.anthonysottile.kenken.SignNumber

import junit.framework.TestCase

import org.json.JSONException
import org.json.JSONObject

class SignNumberTests : TestCase() {

    fun testGetSign() {
        val signValue = Sign.Add
        val signNumber = 15
        val test = SignNumber(signValue, signNumber)
        assertEquals(test.sign, signValue)
    }

    fun testGetNumber() {
        val signValue = Sign.Add
        val signNumber = 15
        val test = SignNumber(signValue, signNumber)
        assertEquals(test.number, signNumber)
    }

    fun testToString() {
        val signValue = Sign.Add
        val signNumber = 15
        val test = SignNumber(signValue, signNumber)
        assertEquals(test.toString(), "15 +")
    }

    fun testSignNumberSignInt() {
        val signValue = Sign.Add
        val signNumber = 15
        val test = SignNumber(signValue, signNumber)
        assertEquals(test.sign, signValue)
        assertEquals(test.number, signNumber)
    }

    fun testSignNumberJSONObject() {
        val json = JSONObject()

        json.put("Sign", Sign.Add.intValue)
        json.put("Number", 15)

        val test = SignNumber(json)
        assertEquals(test.sign, Sign.Add)
        assertEquals(test.number, 15)
    }

    fun testToJson() {
        val test = SignNumber(Sign.Add, 15)

        val json = test.toJson()

        assertEquals(json.getInt("Sign"), Sign.Add.intValue)
        assertEquals(json.getInt("Number"), 15)
    }
}
