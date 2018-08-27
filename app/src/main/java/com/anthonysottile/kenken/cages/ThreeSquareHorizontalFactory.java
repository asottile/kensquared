package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class ThreeSquareHorizontalFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (ThreeSquareHorizontalFactory.factoryInstance == null) {
            ThreeSquareHorizontalFactory.factoryInstance = new ThreeSquareHorizontalFactory();
        }

        return ThreeSquareHorizontalFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Right);
        Point thirdSquare = Points.add(secondSquare, Points.Right);

        return
            game.squareIsValid(location) &&
            game.squareIsValid(secondSquare) &&
            game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareLineCage(game, location, true));
    }

    private ThreeSquareHorizontalFactory() { }
}
