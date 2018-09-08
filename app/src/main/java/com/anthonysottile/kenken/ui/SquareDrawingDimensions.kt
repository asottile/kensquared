package com.anthonysottile.kenken.ui

import android.graphics.Canvas
import android.graphics.Paint
import kotlin.math.ceil

class SquareDrawingDimensions(
        val order: Int,
        private val squareWidth: Int,
        private val squareHeight: Int
) {

    private val cageTextFontSize: Int = squareHeight / 5
    private val valueTextFontSize: Int
    private var candidatesTextFontSize: Int = 0

    private val cageTextPaint = Paint()
    private val valueTextPaint = Paint()
    private val candidatesTextPaint = Paint()


    private fun getLeft(x: Int): Int {
        return UIConstants.BorderWidth * (x + 1) + x * this.squareWidth
    }

    private fun getTop(y: Int): Int {
        return UIConstants.BorderWidth * (y + 1) + y * this.squareHeight
    }

    fun paintCageText(canvas: Canvas, cageText: String, x: Int, y: Int) {
        val left = this.getLeft(x) + SquareDrawingDimensions.cageTextLeftMargin
        val top = this.getTop(y) + this.cageTextFontSize

        canvas.drawText(cageText, left.toFloat(), top.toFloat(), this.cageTextPaint)
    }

    fun paintValueText(canvas: Canvas, valueText: String, x: Int, y: Int) {
        val textWidth = this.valueTextPaint.measureText(valueText).toInt()

        val left = this.getLeft(x) + (this.squareWidth - textWidth) / 2
        val top = this.getTop(y) + this.cageTextFontSize + this.valueTextFontSize

        canvas.drawText(valueText, left.toFloat(), top.toFloat(), this.valueTextPaint)
    }

    fun paintCandidatesText(canvas: Canvas, candidatesText: String, x: Int, y: Int) {
        // Figure out the length of string to print
        val textLength = candidatesText.length

        // Align the candidates text at the bottom of the square
        val left = this.getLeft(x) + 3
        val top = (this.getTop(y) + this.squareHeight - 5
                - this.order / 6 * this.candidatesTextFontSize)

        // Note: this is wrapping character by character, not by word
        // first represents the first character index in the string segment
        // line is the line that the text is being written to
        var first = 0
        var line = 0
        while (first < textLength) {
            var last = candidatesText.length
            while (this.candidatesTextPaint.measureText(candidatesText, first, last) > this.squareWidth - 5) {
                last -= 1
            }

            // Draw the string from first to last
            canvas.drawText(
                    candidatesText,
                    first,
                    last,
                    left.toFloat(),
                    (top + line * this.candidatesTextFontSize).toFloat(),
                    this.candidatesTextPaint
            )

            // assign last into first to check for the next substring
            first = last
            if (first < textLength && candidatesText[first] == ' ') {
                first += 1
            }

            line += 1
        }
    }

    fun paintBackgroundColor(canvas: Canvas, paint: Paint, x: Int, y: Int) {
        val left = this.getLeft(x)
        val right = left + this.squareWidth
        val top = this.getTop(y)
        val bottom = top + this.squareHeight

        canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                paint
        )
    }

    init {
        this.cageTextPaint.textSize = this.cageTextFontSize.toFloat()

        this.valueTextFontSize = (
                this.squareHeight -
                        2 * this.cageTextFontSize -
                        (UIConstants.MaxGameSize - order)
                )
        this.valueTextPaint.textSize = this.valueTextFontSize.toFloat()

        this.candidatesTextFontSize = this.squareHeight - this.cageTextFontSize
        this.candidatesTextPaint.textSize = this.candidatesTextFontSize.toFloat()
        this.candidatesTextPaint.color = UIConstants.CandidatesTextColor
        val testMeasureString = SquareDrawingDimensions.getTestCandidateString(order)

        val maxWidth = this.squareWidth - 5 - (UIConstants.MaxGameSize - order) * 2

        while (this.candidatesTextPaint.measureText(testMeasureString) > maxWidth) {
            this.candidatesTextFontSize -= 1
            this.candidatesTextPaint.textSize = this.candidatesTextFontSize.toFloat()
        }
    }

    companion object {
        private const val cageTextLeftMargin = 5

        private fun getTestCandidateString(order: Int): String {
            // If the order is >= 6 we have two lines of numbers
            val numbers = if (order >= 6) {
                ceil(1.0 * order / 2).toInt()
            } else {
                order
            }
            return IntArray(numbers).joinToString(" ")
        }
    }
}
