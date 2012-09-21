package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

public class OneSquareCage extends BaseCage {

    public OneSquareCage(KenKenGame game, Point location) {
        this.signLocation = location;

        // +---+
        // | X |
        // +---+
        //
        // top, left, bottom, right
        this.renderLines.add(new RenderLine(location, 1, true));
        this.renderLines.add(new RenderLine(location, 1, false));
        this.renderLines.add(new RenderLine(Points.add(location, Points.Down), 1, true));
        this.renderLines.add(new RenderLine(Points.add(location, Points.Right), 1, false));

        this.squares.add(location);

        game.setOccupied(location);

        this.signNumber =
    		CageGenerator.DetermineSign(
				new int[] {
					game.getLatinSquare().getValues()[location.x][location.y]
				}
			);
    }
}
