package com.anthonysottile.kenken.test;

import com.anthonysottile.kenken.Sign;

import junit.framework.TestCase;

public class SignTests extends TestCase {

    public void testGetIntValue() {
        assertEquals(Sign.Add.getIntValue(), 0);
        assertEquals(Sign.Subtract.getIntValue(), 1);
        assertEquals(Sign.Multiply.getIntValue(), 2);
        assertEquals(Sign.Divide.getIntValue(), 3);
        assertEquals(Sign.None.getIntValue(), 4);
    }

    public void testToString() {
        assertEquals(Sign.Add.toString(), "+");
        assertEquals(Sign.Subtract.toString(), "-");
        assertEquals(Sign.Multiply.toString(), "*");
        assertEquals(Sign.Divide.toString(), "/");
        assertEquals(Sign.None.toString(), "");
    }
}
