package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

public class ThreeSquareLineCage extends BaseCage {

    public ThreeSquareLineCage(KenKenGame game, Point location, boolean horizontal) {
        this.signLocation = location;

        Point secondSquare;
        Point thirdSquare;
        if (horizontal) {
            secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getRight());
            thirdSquare = Points.INSTANCE.add(secondSquare, Points.INSTANCE.getRight());

            // +---+---+---+
            // | X | 2 | 3 |
            // +---+---+---+
            //
            // top, left, bottom, right
            this.renderLines.add(new RenderLine(location, 3, true));
            this.renderLines.add(new RenderLine(location, 1, false));
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), 3, true));
            this.renderLines.add(
                    new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(3, Points.INSTANCE.getRight())), 1, false)
            );
        } else {
            secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());
            thirdSquare = Points.INSTANCE.add(secondSquare, Points.INSTANCE.getDown());

            // +---+
            // | X |
            // +---+
            // | 2 |
            // +---+
            // | 3 |
            // +---+
            //
            // top, left, bottom, right
            this.renderLines.add(new RenderLine(location, 1, true));
            this.renderLines.add(new RenderLine(location, 3, false));
            this.renderLines.add(
                    new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(3, Points.INSTANCE.getDown())), 1, true)
            );
            this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), 3, false));
        }

        game.setOccupied(location);
        game.setOccupied(secondSquare);
        game.setOccupied(thirdSquare);

        this.squares.add(location);
        this.squares.add(secondSquare);
        this.squares.add(thirdSquare);

        this.signNumber =
                CageGenerator.DetermineSign
                        (
                                new int[]
                                        {
                                                game.getLatinSquare().getValues()[location.x][location.y],
                                                game.getLatinSquare().getValues()[secondSquare.x][secondSquare.y],
                                                game.getLatinSquare().getValues()[thirdSquare.x][thirdSquare.y]
                                        }
                        );
    }
}
