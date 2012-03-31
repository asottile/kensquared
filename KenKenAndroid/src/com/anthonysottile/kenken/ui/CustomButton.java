package com.anthonysottile.kenken.ui;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class CustomButton extends View {

	private final static int DefaultSize = 28;
	private final static int TopMargin = 8;
	
	// #region Check Changed Event
	
	public class CheckChangedEvent extends EventObject {
		
		private static final long serialVersionUID = 5703235645865455757L;
		
		private boolean checked = false;
		public boolean getChecked() {
			return this.checked;
		}
		
		public CheckChangedEvent(Object object, boolean checked) {
			super(object);
			this.checked = checked;
		}
	}
	public interface CheckChangedListener extends EventListener {
		
		public void onCheckChanged(CheckChangedEvent event);
	}
	private List<CheckChangedListener> checkChangedListeners =
			new ArrayList<CheckChangedListener>();
	public void AddCheckChangedListener(CheckChangedListener listener) {
		this.checkChangedListeners.add(listener);
	}
	public void RemoveCheckChangedListener(CheckChangedListener listener) {
		this.checkChangedListeners.remove(listener);
	}
	public void ClearCheckChangedListeners() {
		this.checkChangedListeners.clear();
	}
	private void triggerCheckChanged() {
        CheckChangedEvent event = new CheckChangedEvent(this, this.checked);
        
        for (CheckChangedListener listener : this.checkChangedListeners) {
        	listener.onCheckChanged(event);
        }
	}
	
	// #endregion
	
	// #region Click Event
	
	public interface ClickListener extends EventListener {
		public void onClick(EventObject event);
	}
	
	private List<ClickListener> clickListeners = new ArrayList<ClickListener>();
	public void AddClickListener(ClickListener listener) {
		this.clickListeners.add(listener);
	}
	public void RemoveClickListener(ClickListener listener) {
		this.clickListeners.remove(listener);
	}
	public void ClearClickListeners() {
		this.clickListeners.clear();
	}
	private void triggerClick() {
		EventObject event = new EventObject(this);
		
		for (ClickListener listener : this.clickListeners) {
			listener.onClick(event);
		}
	}
	
	// #endregion
	
	private int value = 0;
	
	/**
	 * Returns the integer value of this button.
	 * 
	 * @return The value of the button.
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * Sets the integer value of this button.
	 * 
	 * @param value The value to set on the button.
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	private boolean enabled = false;
	
	/**
	 * Returns whether the button is enabled.
	 * 
	 * @return Whether the button is enabled.
	 */
	public boolean getEnabled() {
		return this.enabled;
	}
	
	/**
	 * Sets whether the button is enabled.
	 * 
	 * @param enabled Whether the button should be enabled.
	 */
	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
			this.enabled = enabled;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	private boolean hasLeftCurve = false;
	
	/**
	 * Returns whether the button has a left curve.
	 * 
	 * @return Whether the button has a left curve.
	 */
	public boolean getHasLeftCurve() {
		return this.hasLeftCurve;
	}
	
	/**
	 * Sets whether the button has a left curve.
	 * 
	 * @param hasLeftCurve Whether the button has a left curve.
	 */
	public void setHasLeftCurve(boolean hasLeftCurve) {
		if (this.hasLeftCurve != hasLeftCurve) {
			this.hasLeftCurve = hasLeftCurve;
			
			// Need to redraw
			this.postInvalidate();
		}
	}

	private boolean hasRightCurve = false;
	
	/**
	 * Returns whether the button has a right curve.
	 * 
	 * @return Whether the button has a right curve.
	 */
	public boolean getHasRightCurve() {
		return this.hasRightCurve;
	}
	
	/**
	 * Sets whether the button has a right curve.
	 * 
	 * @param hasRightCurve Whether the button has a right curve.
	 */
	public void setHasRightCurve(boolean hasRightCurve) {
		if (this.hasRightCurve != hasRightCurve) {
			this.hasRightCurve = hasRightCurve;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	private boolean isCheckable = false;
	
	/**
	 * Returns whether the button reacts to clicking by checking and unchecking.
	 * 
	 * @return Whether the button reacts to clicking by checking and unchecking. 
	 */
	public boolean getIsCheckable() {
		return this.isCheckable;
	}
	
	/**
	 * Sets whether the button reacts to clicking by checking and unchecking.
	 * 
	 * @param isCheckable Whether the button reacts to clicking by checking and unchecking.
	 */
	public void setIsCheckable(boolean isCheckable) {
		this.isCheckable = isCheckable;
	}

	private boolean checked = false;
	
	/**
	 * Returns whether the button is checked.
	 * 
	 * @return Whether the button is checked.
	 */
	public boolean getChecked() {
		return this.checked;
	}
	
	/**
	 * Sets whether the button is checked.  Note this triggers check changed.
	 * 
	 * @param checked Whether the button is checked.
	 */
	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			
			this.triggerCheckChanged();
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	/**
	 * Sets whether the button is checked.  Note this does not trigger check changed.
	 * 
	 * @param checked Whether the button is checked.
	 */
	public void setCheckedNoTrigger(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	/**
	 * Toggles the checked state of the button. Note this triggers check changed.
	 */
	public void toggleChecked() {
		this.setChecked(!this.checked);
	}
	
	private String text = "";
	
	/**
	 * Returns the text of the button.
	 * 
	 * @return The text of the button.
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * Sets the text of the button.
	 * 
	 * @param text The display text of the button.
	 */
	public void setText(String text) {
		if (!this.text.equals(text)) {
			this.text = text;
			
			// Redraw
			this.postInvalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// Click event
		if (this.enabled) {
		
			float x = event.getX();
			float y = event.getY();
			
			// Determine if the click position is inside the control
			boolean isInsideControl =
				(x >= 0 && x <= this.getMeasuredWidth())
				&& (y >= 0 && y <= this.getMeasuredHeight());
			
			switch(event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					
					break;
				case MotionEvent.ACTION_UP:
					
					if (isInsideControl) {
						
						if (this.isCheckable) {
							// If we are checkable then change the checked state
							this.setChecked(!this.checked);
						}
						
						this.triggerClick();
					}
					
					break;
			}
		}

		return this.enabled;
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
        	width = CustomButton.DefaultSize;
        	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
        		width = widthSize;
        	}
        }
        if (heightMode == MeasureSpec.EXACTLY) {
        	height = heightSize;
        } else {
        	height = CustomButton.DefaultSize;
        	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
        		height = heightSize;
        	}
        }
        
		this.setMeasuredDimension(width, height);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int start = 1;
		int end = this.getMeasuredWidth() - 1;

		Paint p = UIConstants.GetCageColor();
		
		// Draw left curve
		if (this.hasLeftCurve) {
			start += 7;
			Rect destRect = new Rect(0, 0, 8, this.getMeasuredHeight());
			
			Bitmap bitmap;
			if (!this.enabled) {
				bitmap = BitmapCache.getDisabledLeft();
			} else if (this.checked) {
				bitmap = BitmapCache.getSelectedLeft();
			} else {
				bitmap = BitmapCache.getEnabledLeft();
			}
			
			// Draw left curve
			canvas.drawBitmap(bitmap, null, destRect, p);
				
		} else {
			// Draw 1px left border
			canvas.drawLine(0, 0, 0, this.getMeasuredHeight(), p);
		}
		
		// Draw right curve
		if (this.hasRightCurve) {
			end -= 7;
			Rect destReg = new Rect(
				this.getMeasuredWidth() - 8,
				0,
				this.getMeasuredWidth(),
				this.getMeasuredHeight()
			);
			
			Bitmap bitmap;
			if (!this.enabled) {
				bitmap = BitmapCache.getDisabledRight();
			} else if (this.checked) {
				bitmap = BitmapCache.getSelectedRight();
			} else {
				bitmap = BitmapCache.getEnabledRight();
			}
			
			// Draw right curve
			canvas.drawBitmap(bitmap, null, destReg, p);
			
		} else {
			// Draw 1px right border
			canvas.drawLine(
				this.getMeasuredWidth() - 1,
				0,
				this.getMeasuredWidth() - 1,
				this.getMeasuredHeight(),
				p
			);
		}
		
		// Draw the background
		
		Bitmap bitmap;
		if (!this.enabled) {
			bitmap = BitmapCache.getDisabledCenter();
		} else if (this.checked) {
			bitmap = BitmapCache.getSelectedCenter();
		} else {
			bitmap = BitmapCache.getEnabledCenter();
		}
		
		Rect destReg = new Rect(
			start,
			0,
			end,
			this.getMeasuredHeight()
		);
		
		canvas.drawBitmap(bitmap, null, destReg, p);
		
		// Paint the text
		
		int textSize = this.getMeasuredHeight() - CustomButton.TopMargin * 2;
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(textSize);
		if (!this.enabled) {
			textPaint.setColor(Color.rgb(0x99, 0x99, 0x99));
		} else if (checked) {
			textPaint.setColor(Color.rgb(0, 0, 0));
		} else {
			textPaint.setColor(Color.rgb(0xff, 0xff, 0xff));
		}
		
		float textWidth = textPaint.measureText(this.text);
		int left = (this.getMeasuredWidth() - (int)textWidth) / 2;
		int top = CustomButton.TopMargin + textSize;
		
		canvas.drawText(this.text, left, top, textPaint);
	}
	
	public CustomButton(Context context) {
		super(context);
		
		this.postInvalidate();
	}	
}
