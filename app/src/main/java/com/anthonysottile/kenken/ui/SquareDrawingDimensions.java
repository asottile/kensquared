package com.anthonysottile.kenken.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SquareDrawingDimensions {

    private static final int cageTextLeftMargin = 5;

    private static final int cageFontSizeBase = 33;

    private static int getCageTextFontSize(int order) {
        return SquareDrawingDimensions.cageFontSizeBase - 2 * order;
    }

    private static String getTestCandidateString(int order) {
        // If the order is >= 6 then we are returning only half
        // Otherwise we are returning a full candidates string
        StringBuilder sb = new StringBuilder();
        if (order >= 6) {
            boolean first = true;
            // Round up
            int halfOrder = (int) (1.0 * order / 2 + .9);
            for (int i = 0; i < halfOrder; i += 1) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append(i + 1);
            }
        } else {
            boolean first = true;
            for (int i = 0; i < order; i += 1) {
                if (!first) {
                    sb.append(' ');
                }
                first = false;
                sb.append(i + 1);
            }
        }

        return sb.toString();
    }

    private final int squareWidth;
    private final int squareHeight;
    private final int order;

    private final int cageTextFontSize;
    private final int valueTextFontSize;
    private int candidatesTextFontSize;

    private Paint cageTextPaint;
    private Paint valueTextPaint;
    private Paint candidatesTextPaint;


    private int getLeft(int x) {
        return UIConstants.BorderWidth * (x + 1) + x * this.squareWidth;
    }

    private int getTop(int y) {
        return UIConstants.BorderWidth * (y + 1) + y * this.squareHeight;
    }

    public int getOrder() {
        return this.order;
    }

    /**
     * Paints the cage text for the given square.
     *
     * @param canvas   The canvas to paint on to.
     * @param cageText The cage text to paint on the canvas.
     * @param x        The x index of the square that is painting.
     * @param y        The y index of the square that is painting.
     */
    public void PaintCageText(Canvas canvas, String cageText, int x, int y) {
        int left = this.getLeft(x) + SquareDrawingDimensions.cageTextLeftMargin;
        int top = this.getTop(y) + this.cageTextFontSize;

        canvas.drawText(cageText, left, top, this.cageTextPaint);
    }

    /**
     * Paints the value text for the given square.
     *
     * @param canvas    The canvas to paint on to.
     * @param valueText The value text to paint on the canvas.
     * @param x         The x index of the square that is painting.
     * @param y         The y index of the square that is painting.
     */
    public void PaintValueText(Canvas canvas, String valueText, int x, int y) {

        int textWidth = (int) this.valueTextPaint.measureText(valueText);

        int left = this.getLeft(x) + (this.squareWidth - textWidth) / 2;
        int top = this.getTop(y) + this.cageTextFontSize + this.valueTextFontSize;

        canvas.drawText(valueText, left, top, this.valueTextPaint);
    }

    /**
     * Paints the candidate text for the given square.
     *
     * @param canvas         The canvas to paint on to.
     * @param candidatesText The candidates text to paint on the canvas.
     * @param x              The x index of the square that is painting.
     * @param y              The y index of the square that is painting.
     */
    public void PaintCandidatesText(Canvas canvas, String candidatesText, int x, int y) {
        // Figure out the length of string to print
        int textLength = candidatesText.length();

        // Align the candidates text at the bottom of the square
        int left = this.getLeft(x) + 3;
        int top = this.getTop(y) + this.squareHeight - 5
                - (this.order / 6) * this.candidatesTextFontSize;

        // Note: this is wrapping character by character, not by word
        // first represents the first character index in the string segment
        // line is the line that the text is being written to
        int first = 0;
        int line = 0;
        while (first < textLength) {
            int last = candidatesText.length();
            while (
                    this.candidatesTextPaint.measureText(candidatesText, first, last)
                            > this.squareWidth - 5) {
                last -= 1;
            }

            // Draw the string from first to last
            canvas.drawText(
                    candidatesText,
                    first,
                    last,
                    left,
                    top + line * this.candidatesTextFontSize,
                    this.candidatesTextPaint
            );

            // assign last into first to check for the next substring
            first = last;
            if (first < textLength && candidatesText.charAt(first) == ' ') {
                first += 1;
            }

            line += 1;
        }
    }

    /**
     * Paints the background for the given square.
     *
     * @param canvas The canvas to paint on to.
     * @param paint  The paint to use to draw the background.
     * @param x      The x index of the square that is painting.
     * @param y      The y index of the square that is painting.
     */
    public void PaintBackgroundColor(Canvas canvas, Paint paint, int x, int y) {
        int left = this.getLeft(x);
        int right = left + this.squareWidth;
        int top = this.getTop(y);
        int bottom = top + this.squareHeight;

        canvas.drawRect(
                left,
                top,
                right,
                bottom,
                paint
        );
    }

    public SquareDrawingDimensions(
            int order,
            int squareWidth,
            int squareHeight) {

        this.order = order;
        this.squareWidth = squareWidth;
        this.squareHeight = squareHeight;

        this.cageTextFontSize =
                SquareDrawingDimensions.getCageTextFontSize(this.order);
        this.cageTextPaint = new Paint();
        this.cageTextPaint.setTextSize(this.cageTextFontSize);

        this.valueTextFontSize =
                this.squareHeight
                        - this.cageTextFontSize * 2
                        - 2 * (UIConstants.MaxGameSize - order);
        this.valueTextPaint = new Paint();
        this.valueTextPaint.setTextSize(this.valueTextFontSize);

        this.candidatesTextFontSize = this.squareHeight - this.cageTextFontSize;
        this.candidatesTextPaint = new Paint();
        this.candidatesTextPaint.setTextSize(this.candidatesTextFontSize);
        this.candidatesTextPaint.setColor(UIConstants.CandidatesTextColor);
        String testMeasureString =
                SquareDrawingDimensions.getTestCandidateString(order);

        int maxWidth = this.squareWidth - 5 - (UIConstants.MaxGameSize - order) * 2;

        while (this.candidatesTextPaint.measureText(testMeasureString) > maxWidth) {
            this.candidatesTextFontSize -= 1;
            this.candidatesTextPaint.setTextSize(this.candidatesTextFontSize);
        }
    }
}
