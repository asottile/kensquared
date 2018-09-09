package com.anthonysottile.kenken.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
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

    private var candidatesLayout: CandidatesLayout? = null
    private var valuesLayout: ValuesLayout? = null
    private var timerText: TextView? = null

    private var squareWidthPlusBorder: Int = 0
    private var squareHeightPlusBorder: Int = 0
    private var dimensions: SquareDrawingDimensions? = null

    private var uiSquares: Array<Array<KenKenSquare>>? = null
    private var selectedSquare: KenKenSquare? = null
    private var hoverSquare: KenKenSquare? = null

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
    private val checkClearer = {
        if (this@GameComponent.gameState != GameState.Clear) {
            for (squares in this@GameComponent.uiSquares!!) {
                for (square in squares) {
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

        this.timerText!!.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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

        val userSquares = this.game!!.userSquares

        for (cage in this.game!!.getCages()) {
            if (!cage.cageIsValid(userSquares)) {
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
            val userSquares = this.game!!.userSquares
            val row = square.y
            val col = square.x

            // Remove the value that was set from all candidates in row and col.
            // Except for the row it was set on
            for (i in 0 until order) {
                if (i != row) {
                    userSquares[col][i].removeCandidate(value)
                }

                if (i != col) {
                    userSquares[i][row].removeCandidate(value)
                }
            }

            this.candidatesLayout!!.setDisabled()

            if (this.isGameWon()) {
                // Disable stuff
                this.valuesLayout!!.setDisabled()
                this.candidatesLayout!!.setDisabled()
                this.gameState = GameState.Won

                this.gameTimer.removeCallbacks(this.updater)
                this.updateTime()

                val now = Date()
                val ticks = now.time - this.game!!.gameStartTime.time

                this.triggerGameWon(ticks, order)
            }
        } else {
            this.candidatesLayout!!.setDisabled(this.getDisabled(square))
        }
    }

    private fun getDisabled(square: UserSquare): Set<Int> {
        val order = this.game!!.latinSquare.order
        val x = square.x
        val y = square.y
        val userSquares = this.game!!.userSquares

        val ret = HashSet<Int>()

        for (i in 0 until order) {
            if (i != y && userSquares[x][i].value != 0) {
                ret.add(userSquares[x][i].value)
            }

            if (i != x && userSquares[i][y].value != 0) {
                ret.add(userSquares[i][y].value)
            }
        }

        return ret
    }

    private fun setFromSquare() {
        val currentUserSquare = this.selectedSquare!!.userSquare

        val disabled = this.getDisabled(currentUserSquare)

        this.valuesLayout!!.setDisabled(disabled)
        this.valuesLayout!!.setValue(currentUserSquare.value)

        if (currentUserSquare.value > 0) {
            this.candidatesLayout!!.setDisabled()
        } else {
            this.candidatesLayout!!.setDisabled(disabled)
        }
        this.candidatesLayout!!.setValues(currentUserSquare.candidates)
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
            this.candidatesLayout!!.setDisabled()
            this.valuesLayout!!.setDisabled()
            this.gameTimer.removeCallbacks(this.updater)
            this.timerText!!.text = this.context.getString(R.string.paused)
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
        this.gameTimer.postDelayed(this.checkClearer, 5000)
    }

    private fun initializeGame(order: Int) {
        this.candidatesLayout!!.newGame(order)
        this.valuesLayout!!.newGame(order)

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
        val cages = this.game!!.getCages()
        for (cage in cages) {
            val location = cage.signLocation
            val text = cage.signNumber.toString()
            this.uiSquares!![location.x][location.y].cageText = text
        }

        // Set the first square to be selected
        this.selectedSquare = this.uiSquares!![0][0]
        this.selectedSquare!!.touchState = SquareTouchState.Selected

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
        this.candidatesLayout!!.clear()
        this.valuesLayout!!.clear()

        if (this.game != null) {
            this.gameTimer.removeCallbacks(this.updater)
            this.timerText!!.text = ""

            this.game = null
            this.uiSquares = null
            this.hoverSquare = null
            this.selectedSquare = null
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

        this.candidatesLayout!!.addCandidateAddedListener { candidate ->
            this@GameComponent.selectedSquare!!.userSquare
                    .addCandidate(candidate)
        }

        this.candidatesLayout!!.addCandidateRemovedListener { candidate ->
            this@GameComponent.selectedSquare!!.userSquare
                    .removeCandidate(candidate)
        }

        this.valuesLayout!!.addValueChangedListener { value ->
            this@GameComponent.selectedSquare!!.userSquare.value = value
        }
    }

    private fun getSquareFromPosition(x: Int, y: Int): KenKenSquare {
        var xIndex = (x - UIConstants.BorderWidth) / this.squareWidthPlusBorder
        var yIndex = (y - UIConstants.BorderWidth) / this.squareHeightPlusBorder

        val order = this.game!!.latinSquare.order
        xIndex = min(max(xIndex, 0), order - 1)
        yIndex = min(max(yIndex, 0), order - 1)

        return this.uiSquares!![xIndex][yIndex]
    }

    private fun handleDoubleTap() {
        // Set all 1-square cages
        for (cage in this.game!!.getCages()) {
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
            for (row in this.uiSquares!!) {
                for (uiSquare in row) {
                    val square = uiSquare.userSquare
                    val disabled = this.getDisabled(square)
                    val order = this.game!!.latinSquare.order
                    if (square.value == 0 && disabled.size == order - 1) {
                        var sum = 0
                        for (x in disabled) {
                            sum += x
                        }
                        square.value = order * (order + 1) / 2 - sum
                        actionTaken = true
                    }
                }
            }
        } while (actionTaken)

        // After changing values we need to refresh the number pickers
        this.setFromSquare()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Only want to accept clicks if we are in game.
        if (this.gameState == GameState.InGame) {
            val x = event.x
            val y = event.y

            val targetSquare = this.getSquareFromPosition(x.toInt(), y.toInt())

            // Move, Down are handled as "hovering"
            // Up is handled as the click
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {

                    // If there is a hover square and it is not this one
                    // Then make it not hovered any more.
                    if (this.hoverSquare != null && this.hoverSquare != targetSquare) {
                        this.hoverSquare!!.touchState = SquareTouchState.None
                    }

                    // If this hovering square is not selected then set
                    //  the hover state on it.
                    if (targetSquare.touchState !== SquareTouchState.Selected) {
                        this.hoverSquare = targetSquare
                        this.hoverSquare!!.touchState = SquareTouchState.Touching
                    }
                }
                MotionEvent.ACTION_UP -> {

                    // On mouse up, if there was a hover square then
                    //  clear the hovered state.
                    if (this.hoverSquare != null) {
                        this.hoverSquare!!.touchState = SquareTouchState.None
                        this.hoverSquare = null
                    }

                    // Un set the touched state of the previously touched square
                    // This includes removing its event listeners
                    // Then set the selected square and set the touched state
                    //  and rebind the event handler.
                    this.selectedSquare!!.touchState = SquareTouchState.None
                    this.selectedSquare = targetSquare
                    this.selectedSquare!!.touchState = SquareTouchState.Selected

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

        var boardWidth = this.measuredWidth
        var boardHeight = this.measuredHeight

        // Adjust height / width for borders
        val borders = UIConstants.BorderWidth * (order + 1)
        boardWidth -= borders
        boardHeight -= borders

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
            for (cage in this.game!!.getCages()) {
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
            for (squares in this.uiSquares!!) {
                for (square in squares) {
                    square.drawSquare(canvas, this.dimensions!!)
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
