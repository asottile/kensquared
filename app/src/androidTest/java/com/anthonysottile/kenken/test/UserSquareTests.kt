package com.anthonysottile.kenken.test

import com.anthonysottile.kenken.UserSquare
import junit.framework.TestCase
import java.util.*

class UserSquareTests : TestCase() {
    fun testSetValue() {
        val rowValues = TreeSet<Int>()
        val colValues= TreeSet<Int>()
        val square = UserSquare(rowValues, colValues)

        square.value = 4
        assertTrue(rowValues.contains(4))
        assertTrue(colValues.contains(4))

        square.value = 5
        assertFalse(rowValues.contains(4))
        assertFalse(colValues.contains(4))
        assertTrue(rowValues.contains(5))
        assertTrue(colValues.contains(5))

        square.value = 0
        assertTrue(rowValues.isEmpty())
        assertTrue(colValues.isEmpty())
    }

    fun testCandidatesString() {
        val rowValues = TreeSet<Int>()
        val colValues= TreeSet<Int>()
        val square = UserSquare(rowValues, colValues)

        assertEquals("", square.getCandidatesString())

        square.addCandidate(5)
        square.addCandidate(1)
        assertEquals("1 5", square.getCandidatesString())

        square.removeCandidate(5)
        assertEquals("1", square.getCandidatesString())
    }

    fun testJsonRoundTrip() {
        val rowValues = TreeSet<Int>()
        val colValues= TreeSet<Int>()
        val square = UserSquare(rowValues, colValues)

        square.value = 5
        square.addCandidate(1)
        square.addCandidate(4)

        val square2 = UserSquare(square.toJson(), rowValues, colValues)
        assertEquals(5, square.value)
        assertEquals("1 4", square2.getCandidatesString())
    }
}
