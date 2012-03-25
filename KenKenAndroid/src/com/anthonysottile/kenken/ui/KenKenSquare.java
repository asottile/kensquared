package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.anthonysottile.kenken.SquareDrawingDimensions;
import com.anthonysottile.kenken.UserSquare;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class KenKenSquare {

	public enum SquareTouchState {
		None,
		Touching,
		Selected
	}
		
	private SquareDrawingDimensions dimensions = null;
	
	private UserSquare square = null;
	public UserSquare getUserSquare() {
		return this.square;
	}
	
	private boolean markedIncorrect = false;
	public void setMarkedIncorrect(boolean markedIncorrect) {
		this.markedIncorrect = markedIncorrect;
		this.triggerRequestRedrawEvent();
	}
	
	private SquareTouchState touchState = SquareTouchState.None;
	public SquareTouchState getTouchState() {
		return this.touchState;
	}
	public void setTouchState(SquareTouchState touchState) {
		this.touchState = touchState;
		this.triggerRequestRedrawEvent();
	}
	
	private String cageText = "";
	public void setCageText(String cageText) {
		this.cageText = cageText;
		this.triggerRequestRedrawEvent();
	}
	
	public void drawSquare(Canvas canvas) {
	
		// Fill background
		Paint backgroundColor;
		switch(this.touchState) {
			case Touching:
				backgroundColor = UIConstants.GetHoveringColor();
				break;
			case Selected:
				backgroundColor = UIConstants.GetSelectedColor();
				break;
			case None:
			default:
				backgroundColor = UIConstants.GetBackgroundColor();
				break;
		}
		
		if(this.markedIncorrect) {
			backgroundColor = UIConstants.GetMarkedIncorrectColor();
		}
		
		Rect squareRect = this.dimensions.getSquareRectangle();
		int squareTextTop = squareRect.top + this.dimensions.getSquareTextOffset();
		
		canvas.drawRect(squareRect, backgroundColor);
		
		// Draw cage text
		if(!this.cageText.equalsIgnoreCase("")) {
			Point pos = this.dimensions.getCageTextPosition();
			canvas.drawText(this.cageText, pos.x, pos.y, this.dimensions.getCageTextPaint());
		}
		
		// Draw value text or candidates text
		int squareValue = this.square.getValue();
		if(squareValue > 0) {
			// Display value
			String valueText = this.square.GetValueString();
			Paint textPaint = this.dimensions.getValueTextPaint();
			
			int textWidth = (int)textPaint.measureText(valueText);
			
			int valueTextLeft =	squareRect.left + (squareRect.width() - textWidth) / 2;
			
			canvas.drawText(this.square.GetValueString(), valueTextLeft, squareTextTop, textPaint);
		} else {
			// Display candidates
			String candidatesText = this.square.GetCandidatesString();
			
			// Only paint if there is a string there
			if(!candidatesText.equalsIgnoreCase("")) {
				Paint textPaint = this.dimensions.getCandidateTextPaint();
			
				// Figure out the strings to print
				int squareWidth = squareRect.width();
				int textLength = candidatesText.length();
				
				// Note: this is wrapping character by character, not by word
				// first represents the first character index in the string segment
				// line is the line that the text is being written to
				int first = 0;
				int line = 0;
				while(first < textLength) {
					int last = candidatesText.length();
					while(textPaint.measureText(candidatesText, first, last) > squareWidth) {
						last -= 1;
					}
					
					// Draw the string from first to last
					canvas.drawText(
						candidatesText,
						first,
						last,
						squareRect.left,
						squareTextTop + line * textPaint.getTextSize(),
						textPaint
					);
					
					// assign last into first to check for the next substring
					first = last;
					
					line += 1;
				}
			}
		}
	}
	
	public KenKenSquare(UserSquare square, SquareDrawingDimensions dimensions) {
		this.square = square;
		this.dimensions = dimensions;
		
		final KenKenSquare self = this;
		
		this.square.AddChangedEventHandler(new UserSquare.UserSquareChangedListener() {
			public void onUserSquareChanged(EventObject event) {
				self.triggerRequestRedrawEvent();
			}
		});
	}

	// #region Request Redraw Event
	
	public class RequestRedrawEvent extends EventObject {
		private static final long serialVersionUID = -8285430466228083088L;
		
		private int x;
		public int getX() {
			return this.x;
		}
		private int y;
		public int getY() {
			return this.y;
		}
		
		public RequestRedrawEvent(Object sender, int x, int y) {
			super(sender);
			this.x = x;
			this.y = y;
		}
	}
	public interface RequestRedrawListener extends EventListener {
		public void onRequestRedraw(RequestRedrawEvent event);
	}
	
	private List<RequestRedrawListener> requestRedrawListeners =
			new ArrayList<RequestRedrawListener>();
	public void AddRequestRedrawListener(RequestRedrawListener listener) {
		this.requestRedrawListeners.add(listener);
	}
	public void RemoveRequestRedrawListener(RequestRedrawListener listener) {
		this.requestRedrawListeners.remove(listener);
	}
	public void ClearRequestRedrawListeners() {
		this.requestRedrawListeners.clear();
	}
	private void triggerRequestRedrawEvent() {
		RequestRedrawEvent event =
			new RequestRedrawEvent(
				this,
				this.square.getX(),
				this.square.getY()
			);
		
		int length = this.requestRedrawListeners.size();
		for(int i = 0; i < length; i += 1) {
			this.requestRedrawListeners.get(i).onRequestRedraw(event);
		}
	}
	
	// #endregion
	
}
