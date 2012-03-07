package com.anthonysottile.kenken.ui;

import java.util.List;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SquareDrawingDimensions;
import com.anthonysottile.kenken.cages.ICage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class GameComponent extends View implements KenKenSquare.IRequestRedrawEventHandler {

	private KenKenSquare[][] uiSquares = null;
	
	private KenKenGame game = null;
	public KenKenGame getGame() {
		return this.game;
	}
	
	public void NewGame(int order) {
		
		this.Clear();
		
		this.game = new KenKenGame(order);
		
		int boardWidth = this.getWidth();
		int boardHeight = this.getHeight();
		// Adjust height / width for borders
		int borders = UIConstants.BorderWidth * (order + 1);
		boardWidth -= borders;
		boardHeight -= borders;
		
		int squareWidth = boardWidth / order;
		int squareHeight = boardHeight / order;
		
		this.uiSquares = new KenKenSquare[order][];
		for(int i = 0; i < order; i += 1) {
			this.uiSquares[i] = new KenKenSquare[order];
			for(int j = 0; j < order; j += 1) {
				
				int left = UIConstants.BorderWidth * (i + 1) + i * squareWidth;
				int top = UIConstants.BorderWidth * (j + 1) + j * squareHeight;
				int right = left + squareWidth;
				int bottom = left + squareHeight;
				
				Rect squareRect = new Rect(left, top, right, bottom);
				Point cageTextPosition = new Point(left + 5, top + 5);
				int squareTextOffset = 15;
				
				Paint cageTextPaint = new Paint();
				cageTextPaint.setTextSize(12);
				cageTextPaint.setColor(Color.rgb(0, 0, 0));
				
				Paint valueTextPaint = new Paint();
				valueTextPaint.setTextSize(10);
				valueTextPaint.setColor(Color.rgb(0, 0, 0));
				
				Paint candidateTextPaint = new Paint();
				candidateTextPaint.setTextSize(8);
				candidateTextPaint.setColor(Color.rgb(0, 0, 0));
				
				SquareDrawingDimensions dimensions =
					new SquareDrawingDimensions(
						squareRect,
						cageTextPosition,
						cageTextPaint,
						squareTextOffset,
						valueTextPaint,
						candidateTextPaint
					);
				
				this.uiSquares[i][j] = new KenKenSquare(this.game.getUserSquares()[i][j], dimensions);
				this.uiSquares[i][j].addRequestRedrawEventHandler(this);
			}
		}

		// Invalidate the drawn canvas
		this.postInvalidate();
	}

	public void Clear() {
		// remove event stuff
		int order = this.game.getLatinSquare().getOrder();
		for(int i = 0; i < order; i += 1) {
			for(int j = 0; j < order; j += 1) {
				this.uiSquares[i][j].clearChangedEventHandlers();
			}
		}
		
		this.game = null;
		this.uiSquares = null;
		
		// Invalidate the drawn canvas
		this.postInvalidate();
	}
	
	boolean gridLinesDrawn = false;
	boolean cagesDrawn = false;
	
	public void HandleRequestRedrawEvent(Object sender, KenKenSquare.RequestRedrawEventArgs e) {
		this.postInvalidate();
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int order = this.game.getLatinSquare().getOrder();
		int boardWidth = this.getWidth();
		int boardHeight = this.getHeight();
		// Adjust height / width for borders
		int borders = UIConstants.BorderWidth * (order + 1);
		boardWidth -= borders;
		boardHeight -= borders;
		
		int squareWidth = boardWidth / order;
		int squareHeight = boardHeight / order;
			
		// Draw grids and cages first
		if(!gridLinesDrawn) {
			
			for(int i = 0; i <= order; i += 1) {
				// horizontal grid line
				canvas.drawLine(
					0, i * (squareHeight + UIConstants.BorderWidth),
					this.getWidth(), i * (squareHeight + UIConstants.BorderWidth),
					UIConstants.GetGridColor()
				);
				
				// vertical grid line
				canvas.drawLine(
					i * (squareWidth + UIConstants.BorderWidth), 0,
					i * (squareWidth + UIConstants.BorderWidth), this.getHeight(),
					UIConstants.GetGridColor()
				);
			}
			
			gridLinesDrawn = true;
		}
		
		// Draw Cages
		if(!cagesDrawn) {
			
			List<ICage> cages = this.game.getCages();
			int cagesSize = cages.size();
			for(int i = 0; i < cagesSize; i += 1) {
				ICage cage = cages.get(i);
				List<RenderLine> renderLines = cage.getRenderLines();
				int renderLinesSize = renderLines.size();
				for(int j = 0; j < renderLinesSize; j += 1) {
					RenderLine line = renderLines.get(j);

                    int startX = line.getPosition().x * (squareWidth + UIConstants.BorderWidth);
                    int startY = line.getPosition().y * (squareHeight + UIConstants.BorderWidth);

                    int endX = startX;
                    int endY = startY;

                    if (line.getHorizontal()) {
                        endX += line.getLength() * (squareWidth + UIConstants.BorderWidth);
                    } else {
                        endY += line.getLength() * (squareHeight + UIConstants.BorderWidth);
                    }
                    
                    canvas.drawLine(startX, startY, endX, endY, UIConstants.GetCageColor());				}
			}
			cagesDrawn = true;
		}
		
		// draw the squares themselves
		for(int i = 0; i < order; i += 1) {
			for(int j = 0; j < order; j += 1) {
				this.uiSquares[i][j].drawSquare(canvas);
			}
		}
	}

	public GameComponent(Context context) {
		super(context);
	}	
}
