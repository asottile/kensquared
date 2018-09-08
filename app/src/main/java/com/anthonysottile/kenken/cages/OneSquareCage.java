package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

class OneSquareCage extends BaseCage {

    public OneSquareCage(KenKenGame game, Point location) {
        this.signLocation = location;

        // +---+
        // | X |
        // +---+
        //
        // top, left, bottom, right
        this.renderLines.add(new RenderLine(location, 1, true));
        this.renderLines.add(new RenderLine(location, 1, false));
        this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), 1, true));
        this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), 1, false));

        this.squares.add(location);

        game.setOccupied(location);

        this.signNumber =
                CageGenerator.DetermineSign(
                        new int[]{
                                game.getLatinSquare().getValues()[location.x][location.y]
                        }
                );
    }
}
