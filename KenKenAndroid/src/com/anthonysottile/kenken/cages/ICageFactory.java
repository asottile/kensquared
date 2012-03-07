package com.anthonysottile.kenken.cages;

import com.anthonysottile.kenken.KenKenGame;

import android.graphics.Point;

public interface ICageFactory {

    public boolean CanFit(KenKenGame game, Point location);

    public void ApplyCage(KenKenGame game, Point location);
}
