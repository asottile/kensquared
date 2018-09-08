package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

class ThreeSquareBentCage extends BaseCage {
    public ThreeSquareBentCage(KenKenGame game, Point location, boolean up, boolean left) {
        this.signLocation = location;

        Point secondSquare;
        Point thirdSquare;
        if (up) {
            if (left) {
                secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getRight());
                thirdSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());

                // +---+---+
                // | X | 2 |
                // +---+---+
                // | 3 |
                // +---+
                //
                // top, left, bottom, right, inner bottom, inner right
                this.renderLines.add(new RenderLine(location, 2, true));
                this.renderLines.add(new RenderLine(location, 2, false));
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getDown())), 1, true)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getRight())), 1, false)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), Points.INSTANCE.getRight()), 1, true)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), Points.INSTANCE.getRight()), 1, false)
                );
            } else {
                secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getRight());
                thirdSquare = Points.INSTANCE.add(secondSquare, Points.INSTANCE.getDown());

                // +---+---+
                // | X | 2 |
                // +---+---+
                //     | 3 |
                //     +---+
                //
                // top, left, bottom, right, inner bottom, inner left
                this.renderLines.add(new RenderLine(location, 2, true));
                this.renderLines.add(new RenderLine(location, 1, false));
                this.renderLines.add(
                        new RenderLine(
                                Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getDown())), Points.INSTANCE.getRight()),
                                1, true
                        )
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getRight())), 2, false)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), 1, true)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), Points.INSTANCE.getRight()), 1, false)
                );
            }
        } else {
            if (left) {
                secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());
                thirdSquare = Points.INSTANCE.add(secondSquare, Points.INSTANCE.getRight());

                // +---+
                // | X |
                // +---+---+
                // | 2 | 3 |
                // +---+---+
                //
                // top, left, bottom, right, inner right, inner top
                this.renderLines.add(new RenderLine(location, 1, true));
                this.renderLines.add(new RenderLine(location, 2, false));
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getDown())), 2, true)
                );
                this.renderLines.add(
                        new RenderLine(
                                Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getRight())), Points.INSTANCE.getDown()),
                                1, false
                        )
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), 1, false)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), Points.INSTANCE.getDown()), 1, true)
                );
            } else {
                secondSquare = Points.INSTANCE.add(location, Points.INSTANCE.getDown());
                thirdSquare = Points.INSTANCE.add(secondSquare, Points.INSTANCE.getLeft());

                //     +---+
                //     | X |
                // +---+---+
                // | 3 | 2 |
                // +---+---+
                //
                // top, inner left, right, inner top, left, bottom
                this.renderLines.add(new RenderLine(location, 1, true));
                this.renderLines.add(new RenderLine(location, 1, false));
                this.renderLines.add(new RenderLine(Points.INSTANCE.add(location, Points.INSTANCE.getRight()), 2, false));
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), Points.INSTANCE.getLeft()), 1, true)
                );
                this.renderLines.add(
                        new RenderLine(Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.getDown()), Points.INSTANCE.getLeft()), 1, false)
                );
                this.renderLines.add(
                        new RenderLine(
                                Points.INSTANCE.add(Points.INSTANCE.add(location, Points.INSTANCE.multiply(2, Points.INSTANCE.getDown())), Points.INSTANCE.getLeft()),
                                2, true
                        )
                );
            }
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
