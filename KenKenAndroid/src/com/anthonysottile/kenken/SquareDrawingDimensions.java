package com.anthonysottile.kenken;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

public class SquareDrawingDimensions {

	private Rect squareRectangle;
	public Rect getSquareRectangle() {
		return this.squareRectangle;
	}
	
	private Point cageTextPosition;
	public Point getCageTextPosition() {
		return this.cageTextPosition;
	}
	
	private Paint cageTextPaint;
	public Paint getCageTextPaint() {
		return this.cageTextPaint;
	}
	
	private int squareTextOffset;
	public int getSquareTextOffset() {
		return this.squareTextOffset;
	}
	
	private Paint valueTextPaint;
	public Paint getValueTextPaint() {
		return this.valueTextPaint;
	}
	
	private Paint candidateTextPaint;
	public Paint getCandidateTextPaint() {
		return this.candidateTextPaint;
	}
	
	public SquareDrawingDimensions(
		Rect squareRectangle,
		Point cageTextPosition,
		Paint cageTextPaint,
		int squareTextOffset,
		Paint valueTextPaint,
		Paint candidateTextPaint) {
		
		this.squareRectangle = squareRectangle;
		this.cageTextPosition = cageTextPosition;
		this.cageTextPaint = cageTextPaint;
		this.squareTextOffset = squareTextOffset;
		this.valueTextPaint = valueTextPaint;
		this.candidateTextPaint = candidateTextPaint;
	}
}
