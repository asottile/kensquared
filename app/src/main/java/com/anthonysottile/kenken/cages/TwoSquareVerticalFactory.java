package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;

public class TwoSquareVerticalFactory implements ICageFactory {

    private static ICageFactory factoryInstance;

    public static ICageFactory GetInstance() {
        if (TwoSquareVerticalFactory.factoryInstance == null) {
            TwoSquareVerticalFactory.factoryInstance = new TwoSquareVerticalFactory();
        }

        return TwoSquareVerticalFactory.factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        Point secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());

        return
                game.squareIsValid(location) &&
                        game.squareIsValid(secondSquare);
    }

    public void ApplyCage(KenKenGame game, Point location) {
        game.getCages().add(new TwoSquareCage(game, location, false));
    }

    private TwoSquareVerticalFactory() {
    }

}
