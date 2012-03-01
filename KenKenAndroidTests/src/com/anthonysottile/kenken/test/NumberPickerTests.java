package com.anthonysottile.kenken.test;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import com.anthonysottile.kenken.NumberPicker;

public class NumberPickerTests extends TestCase {

	private final int NumberPickerSize = 5;
	
	private NumberPicker numberPicker = new NumberPicker(NumberPickerSize);
	
	@Test
	public void testGetRemaining() {
		
		// Reset the number picker before the test
		this.numberPicker.Reset();

		// Since the number picker was instantiated with the test size
		assertEquals(NumberPickerSize, numberPicker.GetRemaining());
		
		// Test the entire size
		for(int i = 1; i <= NumberPickerSize; i += 1) {
			try {
				numberPicker.GetNext();
			} catch (Throwable t) {
				fail("GetNext through unexpected exception.");
			}
			assertEquals(NumberPickerSize - i, numberPicker.GetRemaining());
		}
	}

	@Test
	public void testGetNext() {
		
		// Reset the number picker before the test
		this.numberPicker.Reset();
		
		// Instantiate a set to track the integers we receive
		Set<Integer> receivedIntegers = new HashSet<Integer>();
		
		for(int i = 0; i < NumberPickerSize; i += 1) {
			try {
				int receivedInteger = this.numberPicker.GetNext();
				
				// Assert that the integer we got out is in the correct range
				assert(receivedInteger > 0 && receivedInteger <= NumberPickerSize);
				
				// If the insertion into the set fails, then the class is bad.
				assert(receivedIntegers.add(receivedInteger));
				
			} catch (Throwable t) {
				// If an exception was thrown... then bad
				fail("GetNext through unexpected exception.");
			}
		}
		
		// Make sure that the numberPicker throws an exception when empty
		try {
			this.numberPicker.GetNext();
			fail("Failed to Throw Exception when empty.");
		} catch(Exception e) {
			// Everything is OK!
		}
	}

	@Test
	public void testReset() {
		
		// Reset before the test...
		this.numberPicker.Reset();
		
		// After the first reset the number should be equal to size.
		assertEquals(NumberPickerSize, this.numberPicker.GetRemaining());
		
		// Eat two numbers
		try {
			numberPicker.GetNext();
			numberPicker.GetNext();
		} catch (Throwable t) {
			fail("GetNext through unexpected exception.");
		}
		
		// reset and check
		this.numberPicker.Reset();
		assertEquals(NumberPickerSize, this.numberPicker.GetRemaining());
	}

	@Test
	public void testNumberPicker() {
		// Tests for the constructor
		for(int i = 1; i < 5; i += 1) {
			NumberPicker picker = new NumberPicker(i);
			assertEquals(i, picker.GetRemaining());
		}
	}
}
