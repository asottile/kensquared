package com.anthonysottile.kenken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NumberPicker {
	
	private static Random random = new Random();
	private int length = 0;
	private int attempted = 0;
	private List<Integer> collection = new ArrayList<Integer>();

	public int GetRemaining() {
		return this.length - this.attempted;
	}

    public int GetNext()
    {
        if (this.attempted == this.length) {
            return -1;
        }

        int lastCardIndex = this.length - this.attempted - 1;

        int cardDrawn = random.nextInt(this.length - this.attempted);

        // Swap the drawn card with the last card in deck
        int temp = this.collection.get(cardDrawn);
        collection.set(cardDrawn, collection.get(lastCardIndex));
        collection.set(lastCardIndex, temp);

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
