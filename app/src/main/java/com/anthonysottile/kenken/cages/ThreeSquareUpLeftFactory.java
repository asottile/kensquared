package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class ThreeSquareUpLeftFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (ThreeSquareUpLeftFactory.factoryInstance == null) {
            ThreeSquareUpLeftFactory.factoryInstance = new ThreeSquareUpLeftFactory();
        }

        return ThreeSquareUpLeftFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getRight());
        Point thirdSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());

        return
                game.squareIsValid(location) &&
                        game.squareIsValid(secondSquare) &&
                        game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareBentCage(game, location, true, true));
    }

    private ThreeSquareUpLeftFactory() {
    }
}
