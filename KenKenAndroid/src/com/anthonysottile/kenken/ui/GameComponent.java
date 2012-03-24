package com.anthonysottile.kenken.ui;

import java.util.List;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.SettingsProvider;
import com.anthonysottile.kenken.SquareDrawingDimensions;
import com.anthonysottile.kenken.cages.ICage;
import com.anthonysottile.kenken.ui.KenKenSquare.SquareTouchState;
import com.anthonysottile.kenken.ui.ValuesLayout.ValueEvent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameComponent extends View	implements KenKenSquare.IRequestRedrawEventHandler {

	private final static int DefaultSize = 100;
	
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	
	private int squareWidth = -1;
	private int squareWidthPlusBorder = -1;
	private int squareHeight = -1;
	private int squareHeightPlusBorder = -1;
	
	private KenKenSquare[][] uiSquares = null;
	private KenKenSquare currentSelectedSquare = null;
	private KenKenSquare currentHoverSquare = null;
	
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
	
	private void setFromSquare() {
		this.candidatesLayout.SetValues(
			this.currentSelectedSquare.getUserSquare().getCandidates()
		);
		
		this.valuesLayout.SetValue(
			this.currentSelectedSquare.getUserSquare().getValue()
		);
	}
	
	public void Initialize(CandidatesLayout candidatesLayout, ValuesLayout valuesLayout) {
		this.candidatesLayout = candidatesLayout;
		this.valuesLayout = valuesLayout;
		
		final GameComponent self = this;
		
		this.candidatesLayout.AddCandidateAddedListener(
			new CandidatesLayout.CandidateAddedListener() {
				public void onCandidateAdded(CandidatesLayout.CandidateEvent event) {
					self.currentSelectedSquare.getUserSquare().AddCandidate(event.getCandidate());
				}
			}
		);
		
		this.candidatesLayout.AddCandidateRemovedListener(
			new CandidatesLayout.CandidateRemovedListener() {
				public void onCandidateRemoved(CandidatesLayout.CandidateEvent event) {
					self.currentSelectedSquare.getUserSquare().RemoveCandidate(event.getCandidate());
				}
			}
		);
		
		this.valuesLayout.AddValueChangedListener(
			new ValuesLayout.ValueChangedListener() {
				public void onValueChanged(ValueEvent event) {
					self.currentSelectedSquare.getUserSquare().setValue(event.getValue());
				}
			}
		);
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
		
		this.squareWidth = boardWidth / order;
		this.squareHeight = boardHeight / order;
		this.squareWidthPlusBorder = squareWidth + UIConstants.BorderWidth;
		this.squareHeightPlusBorder = squareHeight + UIConstants.BorderWidth;
		
		this.uiSquares = new KenKenSquare[order][];
		for(int i = 0; i < order; i += 1) {
			this.uiSquares[i] = new KenKenSquare[order];
			for(int j = 0; j < order; j += 1) {
				
				int left = UIConstants.BorderWidth * (i + 1) + i * this.squareWidth;
				int top = UIConstants.BorderWidth * (j + 1) + j * this.squareHeight;
				int right = left + this.squareWidth;
				int bottom = top + this.squareHeight;
				
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
		
		// Set the first square to be selected
		this.currentSelectedSquare = this.uiSquares[0][0];
		this.currentSelectedSquare.setTouchState(SquareTouchState.Selected);
		
		/*
		// Test values
		for(int i = 0; i < order; i += 1) {
			for(int j = 0; j < order; j += 1) {
				this.uiSquares[i][j].getUserSquare().setValue(
					this.game.getLatinSquare().getValues()[i][j]
				);
			}
		}
		*/
		
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

	private KenKenSquare getSquareFromPosition(int x, int y) {
		
		int xIndex = (x - UIConstants.BorderWidth) / this.squareWidthPlusBorder;
		int yIndex = (y - UIConstants.BorderWidth) / this.squareHeightPlusBorder;

		int order = this.game.getLatinSquare().getOrder();
		if(xIndex < 0) {
			xIndex = 0;
		}
		if(yIndex < 0) {
			yIndex = 0;
		}
		if(xIndex >= order) {
			xIndex = order - 1;
		}
		if(yIndex >= order) {
			yIndex = order - 1;
		}
		
		return this.uiSquares[xIndex][yIndex];
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// Click event
		
		if(this.game != null) {
			
			float x = event.getX();
			float y = event.getY();
			
			KenKenSquare targetSquare = getSquareFromPosition((int)x, (int)y);
			
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					
					if(this.currentHoverSquare != null && this.currentHoverSquare != targetSquare) {
						this.currentHoverSquare.setTouchState(SquareTouchState.None);
					}

					if(targetSquare.getTouchState() != SquareTouchState.Selected) {
						this.currentHoverSquare = targetSquare;
						this.currentHoverSquare.setTouchState(SquareTouchState.Touching);
					}
					
					break;
				case MotionEvent.ACTION_UP:
					
					if(this.currentHoverSquare != null) {
						this.currentHoverSquare.setTouchState(SquareTouchState.None);
						this.currentHoverSquare = null;
					}
					
					this.currentSelectedSquare.setTouchState(SquareTouchState.None);
					this.currentSelectedSquare = targetSquare;
					this.currentSelectedSquare.setTouchState(SquareTouchState.Selected);
					
					this.setFromSquare();
					
					break;
			}
			
			return true;
		}

		return false;
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
	
	public GameComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.postInvalidate();
	}
}
