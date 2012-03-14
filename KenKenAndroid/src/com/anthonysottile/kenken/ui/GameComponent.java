package com.anthonysottile.kenken.ui;

import java.util.List;

import com.anthonysottile.kenken.IGenericEventHandler;
import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SettingsProvider;
import com.anthonysottile.kenken.SquareDrawingDimensions;
import com.anthonysottile.kenken.cages.ICage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GameComponent extends View
	implements KenKenSquare.IRequestRedrawEventHandler, IGenericEventHandler {

	private final static int DefaultSize = 100;
	
	private KenKenSquare[][] uiSquares = null;
	
	private KenKenGame game = null;
	public KenKenGame getGame() {
		return this.game;
	}
	
	private final int CageFontSizeBase = 24;
	private int getCageTextFontSize(int order) {
		return this.CageFontSizeBase - order;
	}
	
	private final int ValueFontSizeBase = 30;
	private int getValueTextFontSize(int order) {
		return this.ValueFontSizeBase - order;
	}
	
	public void NewGame(int order) {
		
		this.Clear();
		
		this.game = new KenKenGame(order);
		
		int boardWidth = this.getMeasuredWidth();
		int boardHeight = this.getMeasuredHeight();
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
				int bottom = top + squareHeight;
				
				int cageTextFontSize = this.getCageTextFontSize(order);
				int valueTextFontSize = this.getValueTextFontSize(order);
				
				Rect squareRect = new Rect(left, top, right, bottom);
				Point cageTextPosition = new Point(left + 5, top + cageTextFontSize);
				int squareTextOffset = 5 + cageTextFontSize + valueTextFontSize;
				
				Paint cageTextPaint = new Paint();
				cageTextPaint.setTextSize(cageTextFontSize);
				cageTextPaint.setColor(Color.rgb(0, 0, 0));
				
				Paint valueTextPaint = new Paint();
				valueTextPaint.setTextSize(valueTextFontSize);
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
		
		// Pass cage texts into the squares
		List<ICage> cages = this.game.getCages();
		int cagesSize = cages.size();
		for(int i = 0; i < cagesSize; i += 1) {
			ICage cage = cages.get(i);
			Point location = cage.getSignLocation();
			this.uiSquares[location.x][location.y].setCageText(cage.getSignNumber().toString());
		}
		
		// Test values
		for(int i = 0; i < order; i += 1) {
			for(int j = 0; j < order; j += 1) {
				this.uiSquares[i][j].getUserSquare().setValue(
					this.game.getLatinSquare().getValues()[i][j]
				);
			}
		}
		
		// Invalidate the drawn canvas
		this.postInvalidate();
	}

	public void Clear() {
		
		if(this.game != null) {
			// remove event stuff
			int order = this.game.getLatinSquare().getOrder();
			for(int i = 0; i < order; i += 1) {
				for(int j = 0; j < order; j += 1) {
					this.uiSquares[i][j].clearChangedEventHandlers();
				}
			}
			
			this.game = null;
			this.uiSquares = null;
		}
		
		// Invalidate the drawn canvas
		this.postInvalidate();
	}
		
	public void HandleRequestRedrawEvent(Object sender, KenKenSquare.RequestRedrawEventArgs e) {
		this.postInvalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
        int width = -1, height = -1;
        if (widthMode == MeasureSpec.EXACTLY) {
        	width = widthSize;
        } else {
        	width = GameComponent.DefaultSize;
        	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
        		width = widthSize;
        	}
        }
        if (heightMode == MeasureSpec.EXACTLY) {
        	height = heightSize;
        } else {
        	height = GameComponent.DefaultSize;
        	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
        		height = heightSize;
        	}
        }
        
		this.setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
				
		//canvas.drawColor(UIConstants.GetBackgroundColor().getColor());
		
		int order = SettingsProvider.GetGameSize();
				
		int boardWidth = this.getMeasuredWidth();
		int boardHeight = this.getMeasuredHeight();
		// Adjust height / width for borders
		int borders = UIConstants.BorderWidth * (order + 1);
		boardWidth -= borders;
		boardHeight -= borders;
		
		int squareWidth = boardWidth / order;
		int squareHeight = boardHeight / order;
		
		int drawnBoardWidth = order * squareWidth + (order + 1) * UIConstants.BorderWidth;
		int drawnBoardHeight = order * squareHeight + (order + 1) * UIConstants.BorderWidth;
			
		// Draw grids and cages first
		for(int i = 0; i <= order; i += 1) {
			// horizontal grid line
			canvas.drawRect(
				0, i * (squareHeight + UIConstants.BorderWidth),
				drawnBoardWidth, i * squareHeight + (i + 1) * UIConstants.BorderWidth,
				UIConstants.GetGridColor()
			);
			
			// vertical grid line
			canvas.drawRect( 
				i * (squareWidth + UIConstants.BorderWidth), 0,
				i * squareWidth + (i + 1) * UIConstants.BorderWidth, drawnBoardHeight,
				UIConstants.GetGridColor()
			);
		}
		
		if(game != null) {
		
			// Draw Cages
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
                    
                    canvas.drawRect(
                		startX, startY,
                		endX + UIConstants.BorderWidth, endY + UIConstants.BorderWidth,
                		UIConstants.GetCageColor()
            		);
                    
                    
                    //canvas.drawLine(startX, startY, endX, endY, UIConstants.GetCageColor());				
                }
			}
			
			// draw the squares themselves
			for(int i = 0; i < order; i += 1) {
				for(int j = 0; j < order; j += 1) {
					this.uiSquares[i][j].drawSquare(canvas);
				}
			}
		}
	}

	public void HandleGenericEvent(Object sender) {
		// Used to handle the SettingsProvider's set events
		this.Clear();
	}	
	
	public GameComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Attach to the event listener on the game size changed
		SettingsProvider.AddGameSizeChangedEventListener(this);
		
		this.postInvalidate();
	}
}
