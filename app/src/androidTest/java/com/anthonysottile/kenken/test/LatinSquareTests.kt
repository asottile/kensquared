package com.anthonysottile.kenken.test

import com.anthonysottile.kenken.LatinSquare

import junit.framework.TestCase

class LatinSquareTests : TestCase() {

    private val LatinSquareSize = 4
    private val square = LatinSquare(LatinSquareSize)

    fun testGetValues() {
        val values = square.values

        // Assert the sizes of the arrays
        assertEquals(LatinSquareSize, values.size)
        for (i in 0 until LatinSquareSize) {
            assertEquals(LatinSquareSize, values[i].size)
        }

        // Assert that each of the values is in range
        for (i in 0 until LatinSquareSize) {
            for (j in 0 until LatinSquareSize) {
                assert(values[i][j] in 1..LatinSquareSize)
            }
        }

        // Assert that each row and column has the correct sum
        for (i in 0 until LatinSquareSize) {
            var rowSum = 0
            var colSum = 0
            for (j in 0 until LatinSquareSize) {
                rowSum += values[j][i]
                colSum += values[i][j]
            }

            // sum(i, i = 1..n) = n * (n + 1) / 2
            assertEquals(LatinSquareSize * (LatinSquareSize + 1) / 2, rowSum)
            assertEquals(LatinSquareSize * (LatinSquareSize + 1) / 2, colSum)
        }
    }

    fun testGetOrder() {
        assertEquals(LatinSquareSize, this.square.order)
    }

    fun testLatinSquare() {
        for (i in 4..9) {
            val s = LatinSquare(i)
            assertEquals(i, s.order)
        }
    }
}
