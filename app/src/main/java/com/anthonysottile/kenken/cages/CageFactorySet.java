package com.anthonysottile.kenken.cages;

import java.util.Random;


public class CageFactorySet {

	private class WeightedTuple {
		private int weight;
		public int getWeight() {
			return this.weight;
		}

		private ICageFactory factory;
		public ICageFactory getFactory() {
			return this.factory;
		}

		public WeightedTuple(int weight, ICageFactory factory) {
			this.weight = weight;
			this.factory = factory;
		}
	}

    private final Random random = new Random();

    private WeightedTuple[] weightedCollection;
    private int length;
    private int attempted;
    private int weightSum;
    private int weightLeft;

	public CageFactorySet(ICageFactory[] factories, int[] weights) {
		this.attempted = 0;
		this.length = factories.length;

		this.weightSum = CageGenerator.sum(weights);
		this.weightLeft = this.weightSum;

		// Populate the weighted collection
		this.weightedCollection = new WeightedTuple[factories.length];
		for (int i = 0; i < factories.length; i += 1) {
			this.weightedCollection[i] = new WeightedTuple(weights[i], factories[i]);
		}
	}

	public void Reset() {
		this.attempted = 0;
		this.weightLeft = this.weightSum;
	}

	public boolean hasFactoriesLeft() {
		return this.weightLeft > 0;
	}

	public ICageFactory GetFactory() {
		if (this.weightLeft == 0) {
			return null;
		}

        int weightDrawn = this.random.nextInt(this.weightLeft);

        ICageFactory returnValue = null;

        // Find the WeightedTuple to return
        int runningSum = 0;
        int lastCardIndex = this.length - this.attempted - 1;
        for (int i = 0; i <= lastCardIndex; i += 1) {
        	runningSum += this.weightedCollection[i].getWeight();

        	// If we found the drawn weight in the range that is the return value
        	// We need to move it to the end of the drawing array. Reduce the
        	//  weightLeft.  Reduce the count of stuff left. And return that guy.
        	if (weightDrawn < runningSum) {

        		this.weightLeft -= this.weightedCollection[i].getWeight();
        		this.attempted += 1;
        		returnValue = this.weightedCollection[i].getFactory();

        		WeightedTuple temp = this.weightedCollection[i];
        		this.weightedCollection[i] = this.weightedCollection[lastCardIndex];
        		this.weightedCollection[lastCardIndex] = temp;

        		break;
        	}
        }

        return returnValue;
	}
}
