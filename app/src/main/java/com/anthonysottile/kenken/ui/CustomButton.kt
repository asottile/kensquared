package com.anthonysottile.kenken.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import java.util.*

class CustomButton(
        context: Context,
        uiEnabled: Boolean,
        checked: Boolean,
        val value: Int,
        val text: String,
        private val hasLeftCurve: Boolean,
        private val hasRightCurve: Boolean,
        private val isCheckable: Boolean
) : View(context) {
    private val destRect = Rect()
    private val textPaint = Paint()

    private val checkChangedListeners = ArrayList<(obj: CustomButton) -> Unit>()
    private val clickListeners = ArrayList<(obj: CustomButton) -> Unit>()

    var uiEnabled = uiEnabled
        set(enabled) {
            if (field != enabled) {
                field = enabled
                this.postInvalidate()
            }
        }

    private var checkedVal: Boolean = checked

    var checked: Boolean
        get() = this.checkedVal
        set(checked) {
            if (this.checkedVal != checked) {
                this.checkedVal = checked
                this.triggerCheckChanged()
                this.postInvalidate()
            }
        }

    fun setCheckedNoTrigger(checked: Boolean) {
        if (this.checkedVal != checked) {
            this.checkedVal = checked
            this.postInvalidate()
        }
    }

    fun addCheckChangedListener(listener: (obj: CustomButton) -> Unit) {
        this.checkChangedListeners.add(listener)
    }

    private fun triggerCheckChanged() {
        for (listener in this.checkChangedListeners) {
            listener(this)
        }
    }

    fun addClickListener(listener: (obj: CustomButton) -> Unit) {
        this.clickListeners.add(listener)
    }

    private fun triggerClick() {
        for (listener in this.clickListeners) {
            listener(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Click event
        if (this.uiEnabled) {
            val x = event.x
            val y = event.y

            // Determine if the click position is inside the control
            val isInsideControl = x >= 0 && x <= this.measuredWidth && y >= 0 && y <= this.measuredHeight

            when (event.action) {
                MotionEvent.ACTION_UP ->
                    if (isInsideControl) {
                        if (this.isCheckable) {
                            // If we are checkable then change the checked state
                            this.checked = !this.checked
                        }

                        this.triggerClick()
                    }
            }
        }

        return this.uiEnabled
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var width: Int
        var height: Int
        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            width = CustomButton.DefaultSize
            if (widthMode == View.MeasureSpec.AT_MOST && width > widthSize) {
                width = widthSize
            }
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = CustomButton.DefaultSize
            if (heightMode == View.MeasureSpec.AT_MOST && height > heightSize) {
                height = heightSize
            }
        }

        this.setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var start = 1
        var end = this.measuredWidth - 1

        // Draw left curve
        if (this.hasLeftCurve) {
            start += 7
            this.destRect.set(0, 0, 8, this.measuredHeight)

            val bitmap = if (!this.uiEnabled) {
                BitmapCache.disabledLeft
            } else if (this.checked) {
                BitmapCache.selectedLeft
            } else {
                BitmapCache.enabledLeft
            }

            // Draw left curve
            canvas.drawBitmap(bitmap, null, this.destRect, paint)

        } else {
            // Draw 1px left border
            canvas.drawLine(0f, 0f, 0f, this.measuredHeight.toFloat(), paint)
        }

        // Draw right curve
        if (this.hasRightCurve) {
            end -= 7
            this.destRect.set(
                    this.measuredWidth - 8,
                    0,
                    this.measuredWidth,
                    this.measuredHeight
            )

            val bitmap = if (!this.uiEnabled) {
                BitmapCache.disabledRight
            } else if (this.checked) {
                BitmapCache.selectedRight
            } else {
                BitmapCache.enabledRight
            }

            // Draw right curve
            canvas.drawBitmap(bitmap, null, this.destRect, paint)

        } else {
            // Draw 1px right border
            canvas.drawLine(
                    (this.measuredWidth - 1).toFloat(),
                    0f,
                    (this.measuredWidth - 1).toFloat(),
                    this.measuredHeight.toFloat(),
                    paint
            )
        }

        // Draw the background
        val bitmap = if (!this.uiEnabled) {
            BitmapCache.disabledCenter
        } else if (this.checked) {
            BitmapCache.selectedCenter
        } else {
            BitmapCache.enabledCenter
        }

        this.destRect.set(
                start,
                0,
                end,
                this.measuredHeight
        )

        canvas.drawBitmap(bitmap, null, this.destRect, paint)

        // Paint the text

        val textSize = this.measuredHeight - CustomButton.TopMargin * 2

        this.textPaint.textSize = textSize.toFloat()
        if (!this.uiEnabled) {
            this.textPaint.color = Color.rgb(0x99, 0x99, 0x99)
        } else if (this.checked) {
            this.textPaint.color = Color.rgb(0, 0, 0)
        } else {
            this.textPaint.color = Color.rgb(0xff, 0xff, 0xff)
        }

        val textWidth = this.textPaint.measureText(this.text)
        val left = (this.measuredWidth - textWidth.toInt()) / 2
        val top = CustomButton.TopMargin + textSize

        canvas.drawText(this.text, left.toFloat(), top.toFloat(), this.textPaint)
    }

    companion object {
        private const val DefaultSize = 28
        private const val TopMargin = 8
        private val paint = Paint()
    }
}
