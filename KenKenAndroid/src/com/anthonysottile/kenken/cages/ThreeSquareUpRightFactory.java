package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class ThreeSquareUpRightFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (ThreeSquareUpRightFactory.factoryInstance == null) {
            ThreeSquareUpRightFactory.factoryInstance = new ThreeSquareUpRightFactory();
        }

        return ThreeSquareUpRightFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Right);
        Point thirdSquare = Points.add(secondSquare, Points.Down);

        return
            game.squareIsValid(location) &&
            game.squareIsValid(secondSquare) &&
            game.squareIsValid(thirdSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new ThreeSquareBentCage(game, location, true, false));
    }

    private ThreeSquareUpRightFactory() { }
}
