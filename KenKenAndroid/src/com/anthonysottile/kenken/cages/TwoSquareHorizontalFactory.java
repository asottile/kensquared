package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class TwoSquareHorizontalFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (TwoSquareHorizontalFactory.factoryInstance == null) {
            TwoSquareHorizontalFactory.factoryInstance = new TwoSquareHorizontalFactory();
        }

        return TwoSquareHorizontalFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.add(location, Points.Right);

        return
            game.squareIsValid(location) &&
            game.squareIsValid(secondSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new TwoSquareCage(game, location, true));
    }

    private TwoSquareHorizontalFactory() {}

}
