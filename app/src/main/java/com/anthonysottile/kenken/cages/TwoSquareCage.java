package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

public class TwoSquareCage extends BaseCage {

    public TwoSquareCage(KenKenGame game, Point location, boolean horizontal) {
        this.signLocation = location;

        Point secondSquare;
        if (horizontal) {
            secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getRight());

            // +---+---+
            // | X   2 |
            // +---+---+
            //
            // top, left, bottom, right
            this.renderLines.add(new RenderLine(location, 2, true));
            this.renderLines.add(new RenderLine(location, 1, false));
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), 2, true));
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getRight())), 1, false));
        } else {
            secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());

            // +---+
            // | X |
            // +   +
            // | 2 |
            // +---+
            //
            // top, left, bottom, right
            this.renderLines.add(new RenderLine(location, 1, true));
            this.renderLines.add(new RenderLine(location, 2, false));
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getDown())), 1, true));
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), 2, false));
        }

        // set all the squares as occupied
        game.setOccupied(location);
        game.setOccupied(secondSquare);

        this.squares.add(location);
        this.squares.add(secondSquare);

        this.signNumber =
                CageGenerator.DetermineSign
                        (
                                new int[]
                                        {
                                                game.getLatinSquare().getValues()[location.x][location.y],
                                                game.getLatinSquare().getValues()[secondSquare.x][secondSquare.y]
                                        }
                        );
    }
}
