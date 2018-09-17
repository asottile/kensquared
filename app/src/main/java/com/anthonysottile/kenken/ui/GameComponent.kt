package com.anthonysottile.kenken.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.anthonysottile.kenken.KenKenGame
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.UserSquare
import com.anthonysottile.kenken.settings.SettingsProvider
import com.anthonysottile.kenken.ui.KenKenSquare.SquareTouchState
import org.json.JSONObject
import java.util.*
import kotlin.math.max
import kotlin.math.min

class GameComponent(context: Context, attrs: AttributeSet) : View(context, attrs) {
    enum class GameState {
        Clear,
        InGame,
        Paused,
        Won
    }

    private lateinit var candidatesLayout: CandidatesLayout
    private lateinit var valuesLayout: ValuesLayout
    private lateinit var timerText: TextView

    private var squareWidthPlusBorder: Int = 0
    private var squareHeightPlusBorder: Int = 0
    private var dimensions: SquareDrawingDimensions? = null

    private var uiSquares: Array<Array<KenKenSquare>>? = null
    private var selected: Point = Point(0, 0)
    private var hover: Point? = null

    private val selectedSquare: KenKenSquare
        get() {
            return this.uiSquares!![this.selected.x][this.selected.y]
        }

    var gameState = GameState.Clear
        private set

    private var pausedTime: Long = 0

    private var game: KenKenGame? = null

    private val gameTimer = Handler()
    private val updater = object : Runnable {
        override fun run() {
            this@GameComponent.updateTime()
            this@GameComponent.gameTimer.postDelayed(this, 1000)
        }
    }
    private fun checkClearer() {
        if (this.gameState != GameState.Clear) {
            for (row in this.uiSquares!!) {
                for (square in row) {
                    square.markedIncorrect = false
                }
            }
        }
    }

    private var lastTapTime: Long = 0

    private val gameWonListeners = ArrayList<(Long, Int) -> Unit>()

    private fun updateTime() {
        val now = Date()
        val ticks = now.time - this.game!!.gameStartTime.time
        val tickSeconds = ticks.toInt() / 1000
        val seconds = tickSeconds % 60
        val minutes = tickSeconds / 60 % 60
        val hours = tickSeconds / 3600

        this.timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun isGameWon(): Boolean {
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
        if (this.game == null) {
            return false
        }

        val order = this.game!!.latinSquare.order
        if (this.game!!.squaresWithValues < order * order) {
            return false
        }

        for (cage in this.game!!.cages) {
            if (!cage.cageIsValid(this.game!!.userSquares)) {
                return false
            }
        }

        return true
    }

    private fun valueSetEvent(square: UserSquare) {
        val value = square.value

        // ValueSet can be triggered with 0 (unset)
        // We only care about clearing out candidates and
        //  checking for game won in the case where an actual value is set
        if (value > 0) {
            val order = this.game!!.latinSquare.order

            this.candidatesLayout.setDisabled()

            if (this.isGameWon()) {
                // Disable stuff
                this.valuesLayout.setDisabled()
                this.candidatesLayout.setDisabled()
                this.gameState = GameState.Won

                this.gameTimer.removeCallbacks(this.updater)
                this.updateTime()

                val ticks = Date().time - this.game!!.gameStartTime.time

                this.triggerGameWon(ticks, order)
            }
        } else {
            this.candidatesLayout.setDisabled(this.getDisabled(this.selected))
        }
    }

    private fun getDisabled(pos: Point): Set<Int> {
        val ret = this.game!!.rowValues[pos.x] + this.game!!.colValues[pos.y]
        return ret - this.uiSquares!![pos.x][pos.y].userSquare.value
    }

    private fun setFromSquare() {
        val disabled = this.getDisabled(this.selected)

        this.valuesLayout.setDisabled(disabled)
        this.valuesLayout.setValue(this.selectedSquare.userSquare.value)

        if (this.selectedSquare.userSquare.value > 0) {
            this.candidatesLayout.setDisabled()
        } else {
            this.candidatesLayout.setDisabled(disabled)
        }
        this.candidatesLayout.setValues(this.selectedSquare.userSquare.candidates)
    }

    fun pauseIfNotPaused() {
        if (this.gameState == GameState.InGame) {
            this.togglePause()
        }
    }

    fun togglePause() {
        if (this.gameState == GameState.Paused) {
            this.game!!.resetGameStartTime(this.pausedTime)

            this.gameState = GameState.InGame
            this.setFromSquare()
            this.updateTime()
            this.gameTimer.postDelayed(this.updater, 1000)
        } else {
            val date = Date()
            this.pausedTime = date.time - this.game!!.gameStartTime.time

            this.gameState = GameState.Paused
            this.candidatesLayout.setDisabled()
            this.valuesLayout.setDisabled()
            this.gameTimer.removeCallbacks(this.updater)
            this.timerText.text = this.context.getString(R.string.paused)
        }

        // Redraw since paused state changed
        this.postInvalidate()
    }

    fun check() {
        // Penalize the game playing time by 15 seconds
        this.game!!.penalizeGameStartTime(15000)
        this.updateTime()

        val latinSquare = this.game!!.latinSquare

        for ((i, row) in this.uiSquares!!.withIndex()) {
            for ((j, square) in row.withIndex()) {
                val value = square.userSquare.value
                if (value > 0 && value != latinSquare.values[i][j]) {
                    square.markedIncorrect = true
                }
            }
        }

        // Set a timer to clear the UI state change
        this.gameTimer.postDelayed(this::checkClearer, 5000)
    }

    private fun initializeGame(order: Int) {
        this.candidatesLayout.newGame(order)
        this.valuesLayout.newGame(order)

        val userSquares = this.game!!.userSquares

        this.uiSquares = Array(order) { i ->
            Array(order) { j -> KenKenSquare(userSquares[i][j]) }
        }

        for (i in 0 until order) {
            for (j in 0 until order) {
                userSquares[i][j].addValueSetListener(this::valueSetEvent)
                this.uiSquares!![i][j].addRequestRedrawListener(this::postInvalidate)
            }
        }

        // Pass cage texts into the squares
        for (cage in this.game!!.cages) {
            val location = cage.signLocation
            val text = cage.signNumber.toString()
            this.uiSquares!![location.x][location.y].cageText = text
        }

        // Set the first square to be selected
        this.selected = Point(0, 0)
        this.selectedSquare.touchState = SquareTouchState.Selected

        this.updateTime()
        this.gameTimer.postDelayed(this.updater, 1000)

        this.gameState = GameState.InGame

        // Invalidate the drawn canvas
        this.postInvalidate()
    }

    /**
     * Saves out current state of the game.  Returns null if none to save.
     *
     * @return Game JSON or null if there is no game.
     */
    fun saveState(): JSONObject? {
        if (this.gameState == GameState.Clear || this.gameState == GameState.Won) {
            return null
        }

        // If we are paused, we want to set the correct time before saving it out.
        if (this.gameState == GameState.Paused) {
            this.game!!.resetGameStartTime(this.pausedTime)
        }

        val gameAsJson = this.game!!.toJson()

        this.pauseIfNotPaused()

        return gameAsJson
    }

    fun loadState(gameAsJson: JSONObject?) {
        this.clear()
        if (gameAsJson != null) {
            this.game = KenKenGame(gameAsJson)
            this.initializeGame(this.game!!.latinSquare.order)
            this.togglePause()
        }
    }

    fun newGame(gameSize: Int) {
        this.clear()
        this.game = KenKenGame(gameSize)
        this.initializeGame(gameSize)
    }

    fun clear() {
        this.candidatesLayout.clear()
        this.valuesLayout.clear()

        if (this.game != null) {
            this.gameTimer.removeCallbacks(this.updater)
            this.timerText.text = ""

            this.game = null
            this.uiSquares = null
            this.hover = null
            this.selected = Point(0, 0)
        }

        this.gameState = GameState.Clear

        // Invalidate the drawn canvas
        this.postInvalidate()
    }

    /**
     * Gives the Game Component references to the Candidates, Values, and timer text.
     */
    fun initialize(
            candidatesLayout: CandidatesLayout,
            valuesLayout: ValuesLayout,
            timerText: TextView) {

        this.candidatesLayout = candidatesLayout
        this.valuesLayout = valuesLayout
        this.timerText = timerText

        this.candidatesLayout.addCandidateAddedListener { candidate ->
            this.selectedSquare.userSquare.addCandidate(candidate)
        }

        this.candidatesLayout.addCandidateRemovedListener { candidate ->
            this.selectedSquare.userSquare.removeCandidate(candidate)
        }

        this.valuesLayout.addValueChangedListener { value ->
            this.selectedSquare.userSquare.value = value
        }
    }

    private fun getTargetFromPosition(x: Int, y: Int): Point {
        var xIndex = (x - UIConstants.BorderWidth) / this.squareWidthPlusBorder
        var yIndex = (y - UIConstants.BorderWidth) / this.squareHeightPlusBorder

        val order = this.game!!.latinSquare.order
        xIndex = min(max(xIndex, 0), order - 1)
        yIndex = min(max(yIndex, 0), order - 1)

        return Point(xIndex, yIndex)
    }

    private fun handleDoubleTap() {
        // Set all 1-square cages
        for (cage in this.game!!.cages) {
            val cageSquares = cage.squares
            if (cageSquares.size == 1) {
                val pt = cageSquares[0]
                val square = this.uiSquares!![pt.x][pt.y].userSquare
                if (square.value == 0) {
                    square.value = cage.signNumber.number
                }
            }
        }

        // Set all values that can only be satisfied by one value
        do {
            var actionTaken = false
            for ((i, row) in this.uiSquares!!.withIndex()) {
                for ((j, uiSquare) in row.withIndex()) {
                    val square = uiSquare.userSquare
                    val disabled = this.getDisabled(Point(i, j))
                    val order = this.game!!.latinSquare.order
                    if (square.value == 0 && disabled.size == order - 1) {
                        square.value = order * (order + 1) / 2 - disabled.sum()
                        actionTaken = true
                    }
                }
            }
        } while (actionTaken)

        // #5: don't update the pickers if the game is won
        if (this.gameState != GameState.Won) {
            // After changing values we need to refresh the number pickers
            this.setFromSquare()
        }
    }

    private fun unHover() {
        if (this.hover != null) {
            val square = this.uiSquares!![this.hover!!.x][this.hover!!.y]
            square.touchState = SquareTouchState.None
            this.hover = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Only want to accept clicks if we are in game.
        if (this.gameState == GameState.InGame) {
            val x = event.x
            val y = event.y

            val target = this.getTargetFromPosition(x.toInt(), y.toInt())

            // Move, Down are handled as "hovering"
            // Up is handled as the click
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    // If there is a hover square and it is not this one
                    // Then make it not hovered any more.
                    if (this.hover != target) {
                        this.unHover()
                    }

                    // If this hovering square is not selected then set
                    //  the hover state on it.
                    this.hover = target
                    val hoverSquare = this.uiSquares!![this.hover!!.x][this.hover!!.y]
                    if (hoverSquare.touchState === SquareTouchState.None) {
                        hoverSquare.touchState = SquareTouchState.Touching
                    }
                }
                MotionEvent.ACTION_UP -> {

                    // On mouse up, if there was a hover square then clear the
                    // hovered state.
                    this.unHover()

                    // Un set the touched state of the previously touched square
                    // This includes removing its event listeners
                    // Then set the selected square and set the touched state
                    //  and rebind the event handler.
                    this.selectedSquare.touchState = SquareTouchState.None
                    this.selected = target
                    this.selectedSquare.touchState = SquareTouchState.Selected

                    // After this, set up the Candidates and Values layouts.
                    this.setFromSquare()

                    val tapTime = System.currentTimeMillis()
                    if (tapTime - lastTapTime < DOUBLE_TAP_DELTA_MS) {
                        this.handleDoubleTap()
                    }
                    lastTapTime = tapTime
                }
            }

            // Return true since we handled the click
            return true
        }

        return false
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
            width = GameComponent.defaultSize
            if (widthMode == View.MeasureSpec.AT_MOST && width > widthSize) {
                width = widthSize
            }
        }
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = GameComponent.defaultSize
            if (heightMode == View.MeasureSpec.AT_MOST && height > heightSize) {
                height = heightSize
            }
        }

        this.setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // If the game is paused, just draw black background and return
        if (this.gameState == GameState.Paused) {
            canvas.drawColor(Color.BLACK)
            return
        }

        val order = SettingsProvider.gameSize

        val borders = UIConstants.BorderWidth * (order + 1)
        val boardWidth = this.measuredWidth - borders
        val boardHeight = this.measuredHeight - borders

        val squareWidth = boardWidth / order
        val squareHeight = boardHeight / order
        this.squareWidthPlusBorder = squareWidth + UIConstants.BorderWidth
        this.squareHeightPlusBorder = squareHeight + UIConstants.BorderWidth

        if (this.dimensions == null || this.dimensions!!.order != order) {
            this.dimensions = SquareDrawingDimensions(order, squareWidth, squareHeight)
        }

        val drawnBoardWidth = order * squareWidth + (order + 1) * UIConstants.BorderWidth
        val drawnBoardHeight = order * squareHeight + (order + 1) * UIConstants.BorderWidth

        // Draw grids and cages first
        for (i in 0 until order + 1) {
            // horizontal grid line
            canvas.drawRect(
                    0f, (i * (squareHeight + UIConstants.BorderWidth)).toFloat(),
                    drawnBoardWidth.toFloat(), (i * squareHeight + (i + 1) * UIConstants.BorderWidth).toFloat(),
                    UIConstants.getGridColor()
            )

            // vertical grid line
            canvas.drawRect(
                    (i * (squareWidth + UIConstants.BorderWidth)).toFloat(), 0f,
                    (i * squareWidth + (i + 1) * UIConstants.BorderWidth).toFloat(), drawnBoardHeight.toFloat(),
                    UIConstants.getGridColor()
            )
        }

        if (this.gameState == GameState.InGame || this.gameState == GameState.Won) {

            // Draw Cages
            for (cage in this.game!!.cages) {
                for (line in cage.renderLines) {

                    val linePosition = line.position

                    val startX = linePosition.x * (squareWidth + UIConstants.BorderWidth)
                    val startY = linePosition.y * (squareHeight + UIConstants.BorderWidth)

                    var endX = startX
                    var endY = startY

                    if (line.horizontal) {
                        endX += line.length * (squareWidth + UIConstants.BorderWidth)
                    } else {
                        endY += line.length * (squareHeight + UIConstants.BorderWidth)
                    }

                    canvas.drawRect(
                            startX.toFloat(), startY.toFloat(),
                            (endX + UIConstants.BorderWidth).toFloat(), (endY + UIConstants.BorderWidth).toFloat(),
                            UIConstants.getCageColor()
                    )
                }
            }

            // draw the squares themselves
            for ((i, row) in this.uiSquares!!.withIndex()) {
                for ((j, square) in row.withIndex()) {
                    square.drawSquare(canvas, this.dimensions!!, i, j)
                }
            }
        }
    }

    private fun triggerGameWon(ticks: Long, size: Int) {
        for (listener in this.gameWonListeners) {
            listener(ticks, size)
        }
    }

    fun addGameWonListener(listener: (ticks: Long, size: Int) -> Unit) {
        this.gameWonListeners.add(listener)
    }

    companion object {
        private const val defaultSize = 100

        private const val DOUBLE_TAP_DELTA_MS: Long = 300
    }
}
