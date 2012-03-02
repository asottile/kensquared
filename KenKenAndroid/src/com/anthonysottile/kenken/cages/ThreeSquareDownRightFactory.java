package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.*;

public class ThreeSquareDownRightFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (factoryInstance == null)
            factoryInstance = new ThreeSquareDownRightFactory();

        return factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Down);
        Point thirdSquare = Points.add(secondSquare, Points.Left);

        return
            game.squareIsValid(location) &&
            game.squareIsValid(secondSquare) &&
            game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareBentCage(game, location, false, false));
    }

    private ThreeSquareDownRightFactory() { }
}
