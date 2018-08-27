package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;

public interface ICageFactory {

    boolean CanFit(KenKenGame game, Point location);

    void ApplyCage(KenKenGame game, Point location);
}
