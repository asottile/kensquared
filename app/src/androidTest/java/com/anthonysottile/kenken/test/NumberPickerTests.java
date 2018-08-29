package com.anthonysottile.kenken.test;

import com.anthonysottile.kenken.NumberPicker;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class NumberPickerTests extends TestCase {

    private final int NumberPickerSize = 5;

    private NumberPicker numberPicker = new NumberPicker(NumberPickerSize);

    @Test
    public void testGetRemaining() {

        // reset the number picker before the test
        this.numberPicker.reset();

        // Since the number picker was instantiated with the test size
        assertEquals(NumberPickerSize, numberPicker.getRemaining());

        // Test the entire size
        for (int i = 1; i <= NumberPickerSize; i += 1) {
            numberPicker.getNext();
            assertEquals(NumberPickerSize - i, numberPicker.getRemaining());
        }
    }

    @Test
    public void testGetNext() {

        // reset the number picker before the test
        this.numberPicker.reset();

        // Instantiate a set to track the integers we receive
        Set<Integer> receivedIntegers = new HashSet<Integer>();

        for (int i = 0; i < NumberPickerSize; i += 1) {
            int receivedInteger = this.numberPicker.getNext();

            // Assert that the integer we got out is in the correct range
            assert (receivedInteger > 0 && receivedInteger <= NumberPickerSize);

            // If the insertion into the set fails, then the class is bad.
            assert (receivedIntegers.add(receivedInteger));
        }

        // Make sure that the numberPicker throws an exception when empty
        assertEquals(-1, this.numberPicker.getNext());
    }

    @Test
    public void testReset() {

        // reset before the test...
        this.numberPicker.reset();

        // After the first reset the number should be equal to size.
        assertEquals(NumberPickerSize, this.numberPicker.getRemaining());

        // Eat two numbers
        numberPicker.getNext();
        numberPicker.getNext();

        // reset and check
        this.numberPicker.reset();
        assertEquals(NumberPickerSize, this.numberPicker.getRemaining());
    }

    @Test
    public void testNumberPicker() {
        // Tests for the constructor
        for (int i = 1; i < 5; i += 1) {
            NumberPicker picker = new NumberPicker(i);
            assertEquals(i, picker.getRemaining());
        }
    }
}
