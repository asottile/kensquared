package com.anthonysottile.kenken.test

import com.anthonysottile.kenken.NumberPicker
import junit.framework.TestCase
import java.util.*

class NumberPickerTests : TestCase() {

    private val numberPickerSize = 5

    private val numberPicker = NumberPicker(numberPickerSize)

    fun testGetRemaining() {
        // reset the number picker before the test
        this.numberPicker.reset()

        // Since the number picker was instantiated with the test size
        assertEquals(numberPickerSize, numberPicker.getRemaining())

        // Test the entire size
        for (i in 1 until numberPickerSize + 1) {
            numberPicker.getNext()
            assertEquals(numberPickerSize - i, numberPicker.getRemaining())
        }
    }

    fun testGetNext() {
        // reset the number picker before the test
        this.numberPicker.reset()

        // Instantiate a set to track the integers we receive
        val receivedIntegers = HashSet<Int>()

        for (i in 0 until numberPickerSize) {
            val receivedInteger = this.numberPicker.getNext()

            // Assert that the integer we got out is in the correct range
            assert(receivedInteger in 1..numberPickerSize)

            // If the insertion into the set fails, then the class is bad.
            assert(receivedIntegers.add(receivedInteger))
        }

        // Make sure that the numberPicker throws an exception when empty
        assertEquals(-1, this.numberPicker.getNext())
    }

    fun testReset() {
        // reset before the test...
        this.numberPicker.reset()

        // After the first reset the number should be equal to size.
        assertEquals(numberPickerSize, this.numberPicker.getRemaining())

        // Eat two numbers
        numberPicker.getNext()
        numberPicker.getNext()

        // reset and check
        this.numberPicker.reset()
        assertEquals(numberPickerSize, this.numberPicker.getRemaining())
    }

    fun testNumberPicker() {
        // Tests for the constructor
        for (i in 1 until 6) {
            val picker = NumberPicker(i)
            assertEquals(i, picker.getRemaining())
        }
    }
}
