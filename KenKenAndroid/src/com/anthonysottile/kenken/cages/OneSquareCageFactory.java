package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;

public class OneSquareCageFactory implements ICageFactory {

    private static OneSquareCageFactory factoryInstance = null;

    public static ICageFactory GetInstance()
    {
        if (factoryInstance == null)
            factoryInstance = new OneSquareCageFactory();

        return factoryInstance;
    }

    public boolean CanFit(KenKenGame game, Point location) {
        return game.squareIsValid(location);
    }

    public void ApplyCage(KenKenGame game, Point location) {
    	game.getCages().add(new OneSquareCage(game, location));
    }

    // private constructor to disallow instantiation
    private OneSquareCageFactory() { }
}
