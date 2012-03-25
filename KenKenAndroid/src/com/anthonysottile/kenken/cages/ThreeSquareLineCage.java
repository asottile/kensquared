package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.*;

public class ThreeSquareLineCage extends BaseCage {

    public ThreeSquareLineCage(KenKenGame game, Point location, boolean horizontal)
    {
        this.signLocation = location;

        Point secondSquare;
        Point thirdSquare;
        if (horizontal)
        {
            secondSquare = Points.add(location, Points.Right);
            thirdSquare = Points.add(secondSquare, Points.Right);

            // +---+---+---+
            // | X | 2 | 3 |
            // +---+---+---+
            // 
            // top, left, bottom, right
            this.renderLines.add(new RenderLine(location, 3, true));
            this.renderLines.add(new RenderLine(location, 1, false));
    		this.renderLines.add(new RenderLine(Points.add(location,  Points.Down), 3, true));
			this.renderLines.add(
				new RenderLine(Points.add(location, Points.multiply(3, Points.Right)), 1, false)
			);
        }
        else
        {
            secondSquare = Points.add(location, Points.Down);
            thirdSquare = Points.add(secondSquare, Points.Down);

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
				new RenderLine(Points.add(location, Points.multiply(3, Points.Down)), 1, true)
    		);
			this.renderLines.add(new RenderLine(Points.add(location, Points.Right), 3, false));
        }

        game.setOccupied(location);
        game.setOccupied(secondSquare);
        game.setOccupied(thirdSquare);

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
