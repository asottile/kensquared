package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.ICageFactory;
import com.anthonysottile.kenken.KenKenGame;

public class ThreeSquareHorizontalFactory implements ICageFactory {

	public boolean CanFit(KenKenGame game, Point location) {
		// TODO Auto-generated method stub
		return false;
	}

	public void ApplyCage(KenKenGame game, Point location) {
		// TODO Auto-generated method stub

	}

	public static ICageFactory GetInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
