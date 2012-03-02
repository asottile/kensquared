package com.anthonysottile.kenken;

import java.util.Random;

public class CageFactorySet {

    private int length = 0;
    private int attempted = 0;
    private Random random = new Random();
    private ICageFactory[] collection;
	
	public CageFactorySet(ICageFactory[] factories) {
		this.collection = factories;
		this.length = collection.length;
	}

	public void Reset() {
		this.attempted = 0;
	}

	public int getFactoriesLeft() {
		return this.length - this.attempted;
	}

	public ICageFactory GetFactory() {
        if (attempted == length)
            return null;

        int lastCardIndex = length - attempted - 1;

        int cardDrawn = random.nextInt(length - attempted);

        // Swap the drawn card with the last card in deck
        ICageFactory temp = collection[cardDrawn];
        collection[cardDrawn] = collection[lastCardIndex];
        collection[lastCardIndex] = temp;

        attempted++;

        return temp;
	}

}
