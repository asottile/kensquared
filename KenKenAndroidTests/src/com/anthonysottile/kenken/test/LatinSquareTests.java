package com.anthonysottile.kenken.test;

import com.anthonysottile.kenken.LatinSquare;

import junit.framework.TestCase;

public class LatinSquareTests extends TestCase {

	private final int LatinSquareSize = 4;
	private LatinSquare square = new LatinSquare(LatinSquareSize);
	
	public void testGetValues() {
		int[][] values = square.getValues();
		
		// Assert the sizes of the arrays
		assertEquals(LatinSquareSize, values.length);
		for (int i = 0; i < LatinSquareSize; i += 1) {
			assertEquals(LatinSquareSize, values[i].length);
		}
		
		// Assert that each of the values is in range
		for (int i = 0; i < LatinSquareSize; i += 1) {
			for (int j = 0; j < LatinSquareSize; j += 1) {
				assert(values[i][j] > 0 && values[i][j] <= LatinSquareSize);
			}
		}
		
		// Assert that each row and column has the correct sum
		for (int i = 0; i < LatinSquareSize; i += 1) {
			int rowSum = 0;
			int colSum = 0;
			for (int j = 0; j < LatinSquareSize; j += 1) {
				rowSum += values[j][i];
				colSum += values[i][j];
			}

			// sum(i, i = 1..n) = n * (n + 1) / 2
			assertEquals(LatinSquareSize * (LatinSquareSize + 1) / 2, rowSum);
			assertEquals(LatinSquareSize * (LatinSquareSize + 1) / 2, colSum);
		}
	}

	public void testGetOrder() {
		assertEquals(LatinSquareSize, this.square.getOrder());
	}

	public void testLatinSquare() {
		for (int i = 4; i < 9; i += 1) {
			LatinSquare s = new LatinSquare(i);
			assertEquals(i, s.getOrder());
		}
	}
}
