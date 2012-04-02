package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.anthonysottile.kenken.UserSquare;

import android.graphics.Canvas;
import android.graphics.Paint;

public class KenKenSquare {

	public enum SquareTouchState {
		None,
		Touching,
		Selected
	}
		
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
	
	public void drawSquare(Canvas canvas, SquareDrawingDimensions dimensions) {
		int x = this.square.getX();
		int y = this.square.getY();
		
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
		
		if (this.markedIncorrect) {
			backgroundColor = UIConstants.GetMarkedIncorrectColor();
		}
		
		dimensions.PaintBackgroundColor(
			canvas,
			backgroundColor,
			x,
			y
		);
		
		// Draw cage text
		if (this.cageText.length() > 0) {
			dimensions.PaintCageText(
				canvas,
				this.cageText,
				x,
				y
			);
		}
		
		// Draw value text or candidates text
		int squareValue = this.square.getValue();
		if (squareValue > 0) {
			// Display value
			dimensions.PaintValueText(
				canvas,
				this.square.GetValueString(),
				x,
				y
			);
		} else {
			// Display candidates
			String candidatesText = this.square.GetCandidatesString();
			
			// Only paint if there is a string there
			if (candidatesText.length() > 0) {
				dimensions.PaintCandidatesText(
					canvas,
					candidatesText,
					x,
					y
				);
			}
		}
	}
	
	public KenKenSquare(UserSquare square) {
		this.square = square;
		
		this.square.AddChangedEventHandler(new UserSquare.UserSquareChangedListener() {
			public void onUserSquareChanged(EventObject event) {
				KenKenSquare.this.triggerRequestRedrawEvent();
			}
		});
	}

	// #region Request Redraw Event
	
	public interface RequestRedrawListener extends EventListener {
		public void onRequestRedraw(EventObject event);
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
		EventObject event = new EventObject(this);
		
		for (RequestRedrawListener listener : this.requestRedrawListeners) {
			listener.onRequestRedraw(event);
		}
	}
	
	// #endregion
	
}
