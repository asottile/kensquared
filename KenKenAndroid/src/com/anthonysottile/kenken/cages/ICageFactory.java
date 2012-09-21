package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;

public interface ICageFactory {

    public boolean CanFit(KenKenGame game, Point location);

    public void ApplyCage(KenKenGame game, Point location);
}
