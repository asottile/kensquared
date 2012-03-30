package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.json.JSONObject;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.UserSquare;
import com.anthonysottile.kenken.UserSquare.ValueSetEvent;
import com.anthonysottile.kenken.cages.ICage;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.ui.KenKenSquare.RequestRedrawEvent;
import com.anthonysottile.kenken.ui.KenKenSquare.SquareTouchState;
import com.anthonysottile.kenken.ui.ValuesLayout.ValueEvent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class GameComponent extends View {

	private final static int DefaultSize = 100;
	
	private CandidatesLayout candidatesLayout = null;
	private ValuesLayout valuesLayout = null;
	private TextView timerText = null;
	
	private Handler gameTimer = new Handler();
	private Runnable updater = new Runnable() {
		public void run() {
			GameComponent.this.updateTime();
			GameComponent.this.gameTimer.postDelayed(this, 1000);
		}
	};
	
	private final KenKenSquare.RequestRedrawListener redrawListener = 
		new KenKenSquare.RequestRedrawListener() {
			public void onRequestRedraw(RequestRedrawEvent event) {
				GameComponent.this.postInvalidate();
			}
		};
	
	private final UserSquare.ValueSetListener valueSetEventListener =
		new UserSquare.ValueSetListener() {
			public void onValueSet(ValueSetEvent event) {
				GameComponent.this.valueSetEvent(event);
			}
		};
	
	private int squareWidth = -1;
	private int squareWidthPlusBorder = -1;
	private int squareHeight = -1;
	private int squareHeightPlusBorder = -1;
	
	private KenKenSquare[][] uiSquares = null;
	private KenKenSquare selectedSquare = null;
	private KenKenSquare hoverSquare = null;
	
	private boolean paused = false;
	private long pausedTime;
	
	private boolean clickable = false;
	
	private KenKenGame game = null;
	public KenKenGame getGame() {
		return this.game;
	}
	
	private boolean isGameWon() {
		if(this.game == null) {
			return false;
		}
		
		// Note: this is the new version of this check
		// The old check used to make sure every square was filled in
		//  with the values that are stored in the LatinSquare.
		// However, this presented a problem because there were often
		//  multiple solutions to the cages provided that did not necessarily
		//  match that Latin Square.  This new method now checks first to see
		//  if every square has been filled in.  Then it iterates through all
		//  of the cages to see if they are satisfied.
		// This is both faster and makes the game a little easier.
		// However: this produces an inconsistency that will not be resolved
		//  in the fact that the "Check" button will now only reference
		//  the backing square and not one of the several possible solutions.
		
		int order = this.game.getLatinSquare().getOrder();
		if(this.game.getSquaresWithValues() < order * order) {
			return false;
		}
		
		List<ICage> cages = this.game.getCages();
		UserSquare[][] userSquares = this.game.getUserSquares();
		int cagesSize = cages.size();
		for(int i = 0; i < cagesSize; i += 1) {
			if(!cages.get(i).cageIsValid(userSquares)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void updateTime() {
		Date now = new Date();
		long ticks = now.getTime() - this.game.getGameStartTime().getTime();
		int tickSeconds = (int)ticks / 1000;
		int seconds = tickSeconds % 60;
		int minutes = tickSeconds / 60 % 60;
		int hours = tickSeconds / 3600;
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%02d", hours));
		sb.append(':');
		sb.append(String.format("%02d", minutes));
		sb.append(':');
		sb.append(String.format("%02d", seconds));
		
		this.timerText.setText(sb.toString());
	}
	
	private void valueSetEvent(ValueSetEvent event) {
		
		int value = event.getValue();
		
		// ValueSet can be triggered with 0 (unset)
		// We only care about clearing out candidates and
		//  checking for game won in the case where an actual value is set
		if(value > 0) {
			
			int order = this.game.getLatinSquare().getOrder();
			UserSquare[][] userSquares = this.game.getUserSquares();
			int row = event.getY();
			int col = event.getX();
			
			// Remove the value that was set from all candidates in row and col.
			// Except for the row it was set on
			for(int i = 0; i < order; i += 1) {
				
				if(i != row) {
					userSquares[col][i].RemoveCandidate(value);
				}
				
				if(i != col) {
					userSquares[i][row].RemoveCandidate(value);
				}
			}
			
			this.candidatesLayout.SetDisabled();
			
			if(this.isGameWon()) {
				// Disable stuff
				this.valuesLayout.SetDisabled();
				this.candidatesLayout.SetDisabled();
				this.clickable = false;
				
				this.gameTimer.removeCallbacks(this.updater);
				this.updateTime();
				
				Date now = new Date();
				long ticks = now.getTime() - this.game.getGameStartTime().getTime();
				
				this.triggerGameWon(ticks, order);
			}
		} else {
			this.candidatesLayout.SetDisabled(
				this.getDisabled((UserSquare)event.getSource())
			);
		}
	}
	
	private List<Integer> getDisabled(UserSquare square) {
		int order = this.game.getLatinSquare().getOrder();
		int x = square.getX();
		int y = square.getY();
		UserSquare[][] userSquares = game.getUserSquares();
		
		List<Integer> returnList = new ArrayList<Integer>();
		
		for(int i = 0; i < order; i += 1) {
			
			if(i != y && userSquares[x][i].getValue() > 0) {
				returnList.add(userSquares[x][i].getValue());
			}
		
			if(i != x && userSquares[i][y].getValue() > 0) {
				returnList.add(userSquares[i][y].getValue());
			}
		}
		
		return returnList;
	}
	
	private void setFromSquare() {
		
		UserSquare currentUserSquare = this.selectedSquare.getUserSquare();
		
		List<Integer> disabled = this.getDisabled(currentUserSquare);

		this.valuesLayout.SetDisabled(disabled);
		this.valuesLayout.SetValue(currentUserSquare.getValue());
		
		if(currentUserSquare.getValue() > 0) {
			this.candidatesLayout.SetDisabled();
		} else {
			this.candidatesLayout.SetDisabled(disabled);
		}
		this.candidatesLayout.SetValues(currentUserSquare.getCandidates());
	}
		
	/**
	 * Sets the game (if it exists) to a paused state.
	 */
	public void SetPausedIfNotPaused() {
		if(this.game != null && !this.paused) {
			this.TogglePause();
		}
	}
	
	/**
	 * Toggles the paused state of the game.
	 * Do not call if there could be no game at the time.
	 */
	public void TogglePause() {
		if(this.paused) {
			
			// UnPause game
			this.game.ResetGameStartTime(this.pausedTime);
			
			this.paused = false;
			this.clickable = true;
			this.setFromSquare();
			this.updateTime();
			this.gameTimer.postDelayed(this.updater, 1000);
			
		} else {
			
			// Pause game
			Date date = new Date();
			this.pausedTime = date.getTime() - this.game.getGameStartTime().getTime();			
			
			this.paused = true;
			this.clickable = false;
			this.candidatesLayout.SetDisabled();
			this.valuesLayout.SetDisabled();
			this.gameTimer.removeCallbacks(this.updater);
			this.timerText.setText(this.getContext().getString(R.string.paused));
		}
		
		// Redraw since paused state changed
		this.postInvalidate();
	}
	
	/**
	 * Gives the Game Component references to the Candidates, Values, and timer text.
	 */
	public void Initialize(
		CandidatesLayout candidatesLayout,
		ValuesLayout valuesLayout,
		TextView timerText) {
		
		this.candidatesLayout = candidatesLayout;
		this.valuesLayout = valuesLayout;
		this.timerText = timerText;
		
		this.candidatesLayout.AddCandidateAddedListener(
			new CandidatesLayout.CandidateAddedListener() {
				public void onCandidateAdded(CandidatesLayout.CandidateEvent event) {
					GameComponent.this.selectedSquare
						.getUserSquare()
						.AddCandidate(event.getCandidate());
				}
			}
		);
		
		this.candidatesLayout.AddCandidateRemovedListener(
			new CandidatesLayout.CandidateRemovedListener() {
				public void onCandidateRemoved(CandidatesLayout.CandidateEvent event) {
					GameComponent.this.selectedSquare
						.getUserSquare()
						.RemoveCandidate(event.getCandidate());
				}
			}
		);
		
		this.valuesLayout.AddValueChangedListener(
			new ValuesLayout.ValueChangedListener() {
				public void onValueChanged(ValueEvent event) {
					GameComponent.this.selectedSquare
						.getUserSquare()
						.setValue(event.getValue());
				}
			}
		);
	}
	
	private void initializeGame(int order) {
		
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
		
		SquareDrawingDimensions dimensions =
			new SquareDrawingDimensions(order, squareWidth, squareHeight);
		
		UserSquare[][] userSquares = this.game.getUserSquares();
		
		this.uiSquares = new KenKenSquare[order][];
		for(int i = 0; i < order; i += 1) {
			this.uiSquares[i] = new KenKenSquare[order];
			for(int j = 0; j < order; j += 1) {
								
				this.uiSquares[i][j] =
					new KenKenSquare(
						userSquares[i][j],
						dimensions
					);
				this.uiSquares[i][j].AddRequestRedrawListener(this.redrawListener);
			}
		}
		
		// Pass cage texts into the squares
		List<ICage> cages = this.game.getCages();
		int cagesSize = cages.size();
		for(int i = 0; i < cagesSize; i += 1) {
			ICage cage = cages.get(i);
			Point location = cage.getSignLocation();
			this.uiSquares[location.x][location.y]
				.setCageText(cage.getSignNumber().toString());
		}
		
		// Set the first square to be selected
		this.selectedSquare = this.uiSquares[0][0];
		this.selectedSquare.setTouchState(SquareTouchState.Selected);
		
		this.updateTime();
		this.gameTimer.postDelayed(this.updater, 1000);
		
		this.clickable = true;
		
		// Invalidate the drawn canvas
		this.postInvalidate();
	}

	/**
	 * Saves out current state of the game.  Returns null if none to save.
	 * 
	 * @return Game JSON or null if there is no game.
	 */
	public JSONObject SaveState() {
		if(this.game == null) {
			return null;
		}
		
		JSONObject gameAsJson = this.game.ToJson();
		
		// Set paused (which should clean up some things).
		this.SetPausedIfNotPaused();
		
		// Clear the game out
		this.Clear();
		
		return gameAsJson;
	}
	
	public void LoadState(JSONObject gameAsJson) {
		this.Clear();
		if(gameAsJson != null) {
			this.game = new KenKenGame(gameAsJson);
			this.initializeGame(this.game.getLatinSquare().getOrder());
			this.paused = false;
			this.SetPausedIfNotPaused();
		}
	}
	
	public void NewGame(int order) {
		this.Clear();
		this.game = new KenKenGame(order);
		this.initializeGame(order);
	}

	public void Clear() {
		
		if(this.game != null) {
			// remove event stuff
			int order = this.game.getLatinSquare().getOrder();
			for(int i = 0; i < order; i += 1) {
				for(int j = 0; j < order; j += 1) {
					this.uiSquares[i][j].ClearRequestRedrawListeners();
				}
			}
			
			if(this.selectedSquare != null) {
				this.selectedSquare.getUserSquare().ClearValueSetListeners();
			}
			
			this.gameTimer.removeCallbacks(this.updater);
			this.timerText.setText("");
			
			this.game = null;
			this.uiSquares = null;
			this.hoverSquare = null;
			this.selectedSquare = null;
		}
		
		// Invalidate the drawn canvas
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
		
		if(this.game != null && this.clickable) {
			
			float x = event.getX();
			float y = event.getY();
			
			KenKenSquare targetSquare =
				getSquareFromPosition((int)x, (int)y);
			
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					
					if(this.hoverSquare != null
						&& this.hoverSquare != targetSquare) {
						this.hoverSquare.setTouchState(SquareTouchState.None);
					}

					if(targetSquare.getTouchState() != SquareTouchState.Selected) {
						this.hoverSquare = targetSquare;
						this.hoverSquare.setTouchState(SquareTouchState.Touching);
					}
					
					break;
				case MotionEvent.ACTION_UP:
					
					if(this.hoverSquare != null) {
						this.hoverSquare.setTouchState(SquareTouchState.None);
						this.hoverSquare = null;
					}
					
					this.selectedSquare.setTouchState(SquareTouchState.None);
					this.selectedSquare.getUserSquare().RemoveValueSetListener(
						this.valueSetEventListener
					);
					this.selectedSquare = targetSquare;
					this.selectedSquare.setTouchState(SquareTouchState.Selected);
					this.selectedSquare.getUserSquare().AddValueSetListener(
						this.valueSetEventListener
					);
					
					this.setFromSquare();
					
					break;
			}
			
			return true;
		}

		return false;
	}
	
	// #region Measure and Draw
	
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
		
		if(this.paused) {			
			canvas.drawColor(Color.BLACK);
			return;
		}
		
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
	
	// #endregion
	
	// #region Game Won Event
	
	public class GameWonEvent extends EventObject {
		
		private static final long serialVersionUID = -6896411724437170770L;
		
		private long ticks;
		public long getTicks() {
			return this.ticks;
		}
		
		private int size;
		public int getSize() {
			return this.size;
		}
		
		public GameWonEvent(Object sender, long ticks, int size) {
			super(sender);
			
			this.ticks = ticks;
			this.size = size;
		}
	}
	
	public interface GameWonListener extends EventListener {
		public void onGameWon(GameWonEvent event);
	}
	
	private List<GameWonListener> gameWonListeners =
			new ArrayList<GameWonListener>();
	public void AddGameWonListener(GameWonListener listener) {
		this.gameWonListeners.add(listener);
	}
	public void RemoveGameWonListener(GameWonListener listener) {
		this.gameWonListeners.remove(listener);
	}
	private void triggerGameWon(long ticks, int size) {
		GameWonEvent event = new GameWonEvent(this, ticks, size);
		
		int listenersSize = this.gameWonListeners.size();
		for(int i = 0; i < listenersSize; i += 1) {
			this.gameWonListeners.get(i).onGameWon(event);
		}
	}
	
	// #endregion
	
	public GameComponent(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.postInvalidate();
	}
}
