package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberPicker {

	private static Random random = new Random();
	private int length;
	private int attempted = 0;
	private final List<Integer> collection = new ArrayList<Integer>();

	public int GetRemaining() {
		return this.length - this.attempted;
	}

    public int GetNext()
    {
        if (this.attempted == this.length) {
            return -1;
        }

        int lastCardIndex = this.length - this.attempted - 1;

        int cardDrawn = NumberPicker.random.nextInt(this.length - this.attempted);

        // Swap the drawn card with the last card in deck
        int temp = this.collection.get(cardDrawn);
        this.collection.set(cardDrawn, this.collection.get(lastCardIndex));
        this.collection.set(lastCardIndex, temp);

        this.attempted++;

        return temp;
    }

    public void Reset() {
    	this.attempted = 0;
    }

    public NumberPicker(int n) {
        for (int i = 1; i <= n; i += 1) {
            this.collection.add(i);
        }

        this.length = n;
    }
}
