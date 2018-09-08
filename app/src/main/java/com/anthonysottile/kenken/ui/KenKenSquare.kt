package com.anthonysottile.kenken.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.anthonysottile.kenken.UserSquare
import java.util.*

class KenKenSquare(val userSquare: UserSquare) {

    enum class SquareTouchState {
        None,
        Touching,
        Selected
    }

    var markedIncorrect = false
        set(markedIncorrect) {
            field = markedIncorrect
            this.triggerRequestRedrawEvent()
        }

    var touchState = SquareTouchState.None
        set(touchState) {
            field = touchState
            this.triggerRequestRedrawEvent()
        }

    var cageText = ""
        set(cageText) {
            field = cageText
            this.triggerRequestRedrawEvent()
        }

    private val requestRedrawListeners = ArrayList<RequestRedrawListener>()

    fun drawSquare(canvas: Canvas, dimensions: SquareDrawingDimensions) {
        val x = this.userSquare.x
        val y = this.userSquare.y

        // Fill background
        var backgroundColor: Paint = when (this.touchState) {
            KenKenSquare.SquareTouchState.Touching -> hoveringColor
            KenKenSquare.SquareTouchState.Selected -> selectedColor
            KenKenSquare.SquareTouchState.None -> defaultColor
        }
        if (this.markedIncorrect) {
            backgroundColor = incorrectColor
        }

        dimensions.PaintBackgroundColor(
                canvas,
                backgroundColor,
                x,
                y
        )

        // Draw cage text
        if (this.cageText.isNotEmpty()) {
            dimensions.PaintCageText(
                    canvas,
                    this.cageText,
                    x,
                    y
            )
        }

        // Draw value text or candidates text
        val squareValue = this.userSquare.value
        if (squareValue > 0) {
            // Display value
            dimensions.PaintValueText(
                    canvas,
                    Integer.toString(squareValue),
                    x,
                    y
            )
        } else {
            // Display candidates
            val candidatesText = this.userSquare.getCandidatesString()

            // Only paint if there is a string there
            if (candidatesText.isNotEmpty()) {
                dimensions.PaintCandidatesText(
                        canvas,
                        candidatesText,
                        x,
                        y
                )
            }
        }
    }

    init {
        this.userSquare.addChangedEventHandler(object : UserSquare.UserSquareChangedListener {
            override fun onUserSquareChanged(event: EventObject) {
                this@KenKenSquare.triggerRequestRedrawEvent()
            }
        })
    }

    interface RequestRedrawListener : EventListener {
        fun onRequestRedraw(event: EventObject)
    }

    fun addRequestRedrawListener(listener: RequestRedrawListener) {
        this.requestRedrawListeners.add(listener)
    }

    fun clearRequestRedrawListeners() {
        this.requestRedrawListeners.clear()
    }

    private fun triggerRequestRedrawEvent() {
        val event = EventObject(this)

        for (listener in this.requestRedrawListeners) {
            listener.onRequestRedraw(event)
        }
    }

    companion object {
        private val hoveringColor = Paint()
        private val selectedColor = Paint()
        private val incorrectColor = Paint()
        private val defaultColor = Paint()

        init {
            hoveringColor.color = Color.rgb(0x1e, 0x77, 0xd3)
            selectedColor.color = Color.rgb(0x00, 0xc0, 0xc0)
            incorrectColor.color = Color.rgb(0xff, 0x66, 0x66)
            defaultColor.color = Color.rgb(0xff, 0xff, 0xff)
        }
    }
}
