package com.anthonysottile.kenken.test

import com.anthonysottile.kenken.Sign

import junit.framework.TestCase

class SignTests : TestCase() {

    fun testGetIntValue() {
        assertEquals(Sign.Add.intValue, 0)
        assertEquals(Sign.Subtract.intValue, 1)
        assertEquals(Sign.Multiply.intValue, 2)
        assertEquals(Sign.Divide.intValue, 3)
        assertEquals(Sign.None.intValue, 4)
    }

    fun testToString() {
        assertEquals(Sign.Add.toString(), "+")
        assertEquals(Sign.Subtract.toString(), "-")
        assertEquals(Sign.Multiply.toString(), "×")
        assertEquals(Sign.Divide.toString(), "÷")
        assertEquals(Sign.None.toString(), "")
    }
}
