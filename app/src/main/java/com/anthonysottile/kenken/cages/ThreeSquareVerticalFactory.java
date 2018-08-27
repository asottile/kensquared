package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class ThreeSquareVerticalFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (ThreeSquareVerticalFactory.factoryInstance == null) {
            ThreeSquareVerticalFactory.factoryInstance = new ThreeSquareVerticalFactory();
        }

        return ThreeSquareVerticalFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Down);
        Point thirdSquare = Points.add(secondSquare, Points.Down);

        return
                game.squareIsValid(location) &&
                        game.squareIsValid(secondSquare) &&
                        game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareLineCage(game, location, false));
    }

    private ThreeSquareVerticalFactory() {
    }
}
