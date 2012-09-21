package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class ThreeSquareDownLeftFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (ThreeSquareDownLeftFactory.factoryInstance == null) {
            ThreeSquareDownLeftFactory.factoryInstance = new ThreeSquareDownLeftFactory();
        }

        return ThreeSquareDownLeftFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Down);
        Point thirdSquare = Points.add(secondSquare, Points.Right);

        return
            game.squareIsValid(location) &&
            game.squareIsValid(secondSquare) &&
            game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareBentCage(game, location, false, true));
    }

    private ThreeSquareDownLeftFactory() { }
}
