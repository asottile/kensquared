package com.anthonysottile.kenken;

import android.graphics.Point;

public interface ICageFactory {

    public boolean CanFit(KenKenGame game, Point location);

    public void ApplyCage(KenKenGame game, Point location);
}
