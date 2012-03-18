package com.anthonysottile.kenken.ui;

import com.anthonysottile.kenken.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CustomButton extends View {
	
	private final static int DefaultSize = 28;
	
	private boolean enabled = false;
	public boolean getEnabled() {
		return this.enabled;
	}
	public void setEnabled(boolean enabled) {
		if(this.enabled != enabled) {
			this.enabled = enabled;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	private boolean hasLeftCurve = false;
	public boolean getHasLeftCurve() {
		return this.hasLeftCurve;
	}
	public void setHasLeftCurve(boolean hasLeftCurve) {
		if(this.hasLeftCurve != hasLeftCurve) {
			this.hasLeftCurve = hasLeftCurve;
			
			// Need to redraw
			this.postInvalidate();
		}
	}

	private boolean hasRightCurve = false;
	public boolean getHasRightCurve() {
		return this.hasRightCurve;
	}
	public void setHasRightCurve(boolean hasRightCurve) {
		if(this.hasRightCurve != hasRightCurve) {
			this.hasRightCurve = hasRightCurve;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	private boolean isCheckable = false;
	public boolean getIsCheckable() {
		return this.isCheckable;
	}
	public void setIsCheckable(boolean isCheckable) {
		this.isCheckable = isCheckable;
	}

	private boolean checked = false;
	public boolean getChecked() {
		return this.checked;
	}
	public void setChecked(boolean checked) {
		if(this.checked != checked) {
			this.checked = checked;
			
			// TODO: trigger check changed
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	public void setCheckedNoTrigger(boolean checked) {
		if(this.checked != checked) {
			this.checked = checked;
			
			// Need to redraw
			this.postInvalidate();
		}
	}
	
	private String text = "";
	public String getText() {
		return this.text;
	}
	public void setText(String text) {
		if(!this.text.equals(text)) {
			this.text = text;
			
			// Redraw
			this.postInvalidate();
		}
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

		Rect srcRect = null;
		
		Resources res = this.getContext().getResources();
		
		Paint p = new Paint();
		p.setColor(Color.rgb(0, 0, 0));
		
		if(this.hasLeftCurve) {
			start += 7;
			Rect destReg = new Rect(0, 0, 8, this.getMeasuredHeight());
			
			Bitmap bitmap;
			if(this.checked) {
				bitmap = BitmapFactory.decodeResource(res, R.drawable.left_selected);
			} else {
				bitmap = BitmapFactory.decodeResource(res, R.drawable.left);
			}
			
			
			// Draw left curve
			canvas.drawBitmap(bitmap, srcRect, destReg, p);
				
		} else {
			// Draw 1px left border
			canvas.drawLine(0, 0, 0, this.getMeasuredHeight(), p);
		}
		
		if(this.hasRightCurve) {
			end -= 7;
			Rect destReg = new Rect(
				this.getMeasuredWidth() - 8,
				0,
				this.getMeasuredWidth(),
				this.getMeasuredHeight()
			);
			
			Bitmap bitmap;
			if(this.checked) {
				bitmap = BitmapFactory.decodeResource(res, R.drawable.right_selected);
			} else {
				bitmap = BitmapFactory.decodeResource(res, R.drawable.right);
			}
			
			// Draw right curve
			canvas.drawBitmap(bitmap, srcRect, destReg, p);
			
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
		

		Bitmap bitmap;
		if(this.checked) {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.middle_selected);
		} else {
			bitmap = BitmapFactory.decodeResource(res, R.drawable.middle);
		}
		
		Rect destReg = new Rect(
			start,
			0,
			end,
			this.getMeasuredHeight()
		);
		
		canvas.drawBitmap(bitmap, srcRect, destReg, p);
		
		Paint textPaint = new Paint();
		textPaint.setTextSize(20);
		if(checked) {
			textPaint.setColor(Color.rgb(0, 0, 0));
		} else {
			textPaint.setColor(Color.rgb(0xff, 0xff, 0xff));
		}
		
		float textWidth = textPaint.measureText(this.text);
		int left = (this.getMeasuredWidth() - (int)textWidth) / 2;
		int top = this.getMeasuredHeight() / 2 + 10;
		
		canvas.drawText(this.text, left, top, textPaint);
	}
	
	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
				
		this.enabled = a.getBoolean(R.styleable.CustomButton_enabled, false);
		this.hasLeftCurve = a.getBoolean(R.styleable.CustomButton_has_left_curve, false);
		this.hasRightCurve = a.getBoolean(R.styleable.CustomButton_has_right_curve, false);
		this.isCheckable = a.getBoolean(R.styleable.CustomButton_is_checkable, false);
		this.checked = a.getBoolean(R.styleable.CustomButton_checked, false);
		this.text = a.getString(R.styleable.CustomButton_text);

		this.postInvalidate();
	}
}
