package com.anthonysottile.kenken.cages;

import java.util.Random;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Sign;
import com.anthonysottile.kenken.SignNumber;

public final class CageGenerator {
	
	private static Random random = new Random();

	private static ICageFactory oneSquareFactory = OneSquareCageFactory.GetInstance();
	
    private static CageFactorySet simpleCageFactories =
        new CageFactorySet(
            new ICageFactory[] {
                TwoSquareHorizontalFactory.GetInstance(),
                TwoSquareVerticalFactory.GetInstance(),
                ThreeSquareVerticalFactory.GetInstance(),
                ThreeSquareHorizontalFactory.GetInstance(),
                ThreeSquareUpLeftFactory.GetInstance(),
                ThreeSquareUpRightFactory.GetInstance(),
                ThreeSquareDownLeftFactory.GetInstance(),
                ThreeSquareDownRightFactory.GetInstance()
            }
        );
	
	private static int max(int[] numbers) {
		int max = numbers[0];
		for(int i = 1; i < numbers.length; i += 1) {
			if(numbers[i] > max) {
				max = numbers[i];
			}
		}
		return max;
	}

	private static int min(int[] numbers) {
		int min = numbers[0];
		for(int i = 1; i < numbers.length; i += 1) {
			if(numbers[i] < min) {
				min = numbers[i];
			}
		}
		return min;
	}
	
	private static int sum(int[] numbers) {
		int sum = 0;
		for(int i = 0; i < numbers.length; i += 1) {
			sum += numbers[i];
		}
		return sum;
	}
	
	private static int product(int[] numbers) {
		int product = 1;
		for(int i = 0; i < numbers.length; i += 1) {
			product *= numbers[i];
		}
		return product;
	}

	public static SignNumber DetermineSign(int[] numbers) {

        // TODO: figure out better sign weighting

        final int maxRand = 100;

        int max = max(numbers);
        int min = min(numbers);

        // (0, 0) divide
        // (0, 0) subtract
        // [0, 50) multiply [50%]
        // [50, 100) add [50%]
        int divideCutOff = 0;
        int subtractCutOff = 0;
        int multiplyCutOff = 50;

        if (numbers.length == 1) {
            // one numbered cages have no sign
            return new SignNumber(Sign.None, numbers[0]);
            
        } else if (numbers.length == 2) {
            // division and subtraction are only valid for two number cages

            // (0, 0) divide [0%]
            // [0, 20) subtract [20%]
            // [20, 55) multiply [35%]
            // [55, 100) add [45%]
            subtractCutOff += 20;
            multiplyCutOff += 5;

            if (max % min == 0)
            {
                // can do division
                // [0, 15) divide [15%]
                // [15, 30) subtract [15%]
                // [30, 65) multiply [35%]
                // [65, 100) add [35%]
                divideCutOff += 15;
                subtractCutOff += 10;
                multiplyCutOff += 10;
            }
        }

        int randomNumber = CageGenerator.random.nextInt(maxRand);
        if (randomNumber < divideCutOff) {
        	
            return new SignNumber(Sign.Divide, max / min);
            
        } else if (randomNumber < subtractCutOff) {
        	
            return new SignNumber(Sign.Subtract, max - min);
            
        } else if (randomNumber < multiplyCutOff) {
        	
            return new SignNumber(Sign.Multiply, CageGenerator.product(numbers));
            
        } else {
            return new SignNumber(Sign.Add, CageGenerator.sum(numbers));
        }
	}

    public static void Generate(KenKenGame game) {
        int order = game.getLatinSquare().getOrder();

        // for now just iterate through the rows and attempt to apply the few shapes I have
        int row = 0;
        while (row < order) {
            int column = 0;
            while (column < order) {
                Point p = new Point(column, row);
                if (!game.squareIsValid(p)) {
                    column++;
                    continue;
                }

                // reset it so we are drawing anew
                simpleCageFactories.Reset();

                boolean appliedACage = false;
                while (simpleCageFactories.getFactoriesLeft() > 0) {
                    ICageFactory factory = simpleCageFactories.GetFactory();

                    if (factory.CanFit(game, p)) {
                        factory.ApplyCage(game, p);
                        appliedACage = true;
                        break;
                    }
                }

                // if no cage was applied, then the 1x1 is the only choice left
                if (!appliedACage) {
                    oneSquareFactory.ApplyCage(game, p);
                }

                column++;
            }
            row++;
        }
    }
	
	private CageGenerator() {}
}
