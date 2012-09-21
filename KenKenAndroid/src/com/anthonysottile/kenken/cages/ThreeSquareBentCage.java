package com.anthonysottile.kenken.cages;

import android.graphics.Point;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.Points;
import com.anthonysottile.kenken.RenderLine;

public class ThreeSquareBentCage extends BaseCage {
	public ThreeSquareBentCage(KenKenGame game, Point location, boolean up, boolean left)
    {
        this.signLocation = location;

        Point secondSquare;
        Point thirdSquare;
        if (up)
        {
            if (left)
            {
                secondSquare = Points.add(location, Points.Right);
                thirdSquare = Points.add(location, Points.Down);

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
            		new RenderLine(Points.add(location, Points.multiply(2, Points.Down)), 1, true)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(location, Points.multiply(2, Points.Right)), 1, false)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(Points.add(location, Points.Down), Points.Right), 1, true)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(Points.add(location, Points.Down), Points.Right), 1, false)
        		);
            }
            else
            {
                secondSquare = Points.add(location, Points.Right);
                thirdSquare = Points.add(secondSquare, Points.Down);

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
        				Points.add(Points.add(location, Points.multiply(2, Points.Down)), Points.Right),
        				1, true
    				)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(location, Points.multiply(2, Points.Right)), 2, false)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(location, Points.Down), 1, true)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(Points.add(location, Points.Down), Points.Right), 1, false)
        		);
            }
        }
        else
        {
            if (left)
            {
                secondSquare = Points.add(location, Points.Down);
                thirdSquare = Points.add(secondSquare, Points.Right);

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
        			new RenderLine(Points.add(location, Points.multiply(2, Points.Down)), 2, true)
    			);
            	this.renderLines.add(
        			new RenderLine(
    					Points.add(Points.add(location, Points.multiply(2, Points.Right)), Points.Down),
    					1, false
					)
    			);
            	this.renderLines.add(
        			new RenderLine(Points.add(location, Points.Right), 1, false)
    			);
            	this.renderLines.add(
        			new RenderLine(Points.add(Points.add(location, Points.Right), Points.Down), 1, true)
    			);
            }
            else
            {
                secondSquare = Points.add(location, Points.Down);
                thirdSquare = Points.add(secondSquare, Points.Left);

                //     +---+
                //     | X |
                // +---+---+
                // | 3 | 2 |
                // +---+---+
                //
                // top, inner left, right, inner top, left, bottom
                this.renderLines.add(new RenderLine(location, 1, true));
                this.renderLines.add(new RenderLine(location, 1, false));
                this.renderLines.add(new RenderLine(Points.add(location, Points.Right), 2, false));
                this.renderLines.add(
            		new RenderLine(Points.add(Points.add(location, Points.Down), Points.Left), 1 , true)
        		);
                this.renderLines.add(
            		new RenderLine(Points.add(Points.add(location, Points.Down), Points.Left), 1 , false)
        		);
                this.renderLines.add(
            		new RenderLine(
        				Points.add(Points.add(location, Points.multiply(2, Points.Down)), Points.Left),
        				2 , true
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
