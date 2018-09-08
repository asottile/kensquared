package com.anthonysottile.kenken.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.anthonysottile.kenken.KenKenGame;
import com.anthonysottile.kenken.LatinSquare;
import com.anthonysottile.kenken.R;
import com.anthonysottile.kenken.RenderLine;
import com.anthonysottile.kenken.UserSquare;
import com.anthonysottile.kenken.UserSquare.ValueSetEvent;
import com.anthonysottile.kenken.cages.ICage;
import com.anthonysottile.kenken.settings.SettingsProvider;
import com.anthonysottile.kenken.ui.KenKenSquare.SquareTouchState;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameComponent extends View {

    public enum GameState {
        Clear,
        InGame,
        Paused,
        Won
    }

    private final static int defaultSize = 100;

    private CandidatesLayout candidatesLayout = null;
    private ValuesLayout valuesLayout = null;
    private TextView timerText = null;

    private final KenKenSquare.RequestRedrawListener redrawListener =
            event -> GameComponent.this.postInvalidate();

    private final UserSquare.ValueSetListener valueSetListener =
            event -> GameComponent.this.valueSetEvent(event);

    private int squareWidthPlusBorder;
    private int squareHeightPlusBorder;
    private SquareDrawingDimensions dimensions = null;

    private KenKenSquare[][] uiSquares = null;
    private KenKenSquare selectedSquare = null;
    private KenKenSquare hoverSquare = null;

    private GameState gameState = GameState.Clear;

    public GameState getGameState() {
        return this.gameState;
    }

    private long pausedTime;

    private KenKenGame game = null;

    public KenKenGame getGame() {
        return this.game;
    }

    private final Handler gameTimer = new Handler();
    private final Runnable updater = new Runnable() {
        public void run() {
            GameComponent.this.updateTime();
            GameComponent.this.gameTimer.postDelayed(this, 1000);
        }
    };
    private final Runnable checkClearer = () -> {
        if (GameComponent.this.gameState != GameState.Clear) {
            for (KenKenSquare[] squares : GameComponent.this.uiSquares) {
                for (KenKenSquare square : squares) {
                    square.setMarkedIncorrect(false);
                }
            }
        }
    };

    private void updateTime() {
        Date now = new Date();
        long ticks = now.getTime() - this.game.getGameStartTime().getTime();
        int tickSeconds = (int) ticks / 1000;
        int seconds = tickSeconds % 60;
        int minutes = tickSeconds / 60 % 60;
        int hours = tickSeconds / 3600;

        String sb =
                String.format("%02d", hours) +
                        ':' +
                        String.format("%02d", minutes) +
                        ':' +
                        String.format("%02d", seconds);

        this.timerText.setText(sb);
    }

    private boolean isGameWon() {
        if (this.game == null) {
            return false;
        }

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

        int order = this.game.getLatinSquare().getOrder();
        if (this.game.getSquaresWithValues() < order * order) {
            return false;
        }

        UserSquare[][] userSquares = this.game.getUserSquares();

        for (ICage cage : this.game.getCages()) {
            if (!cage.cageIsValid(userSquares)) {
                return false;
            }
        }

        return true;
    }

    private void valueSetEvent(ValueSetEvent event) {
        int value = event.getValue();

        // ValueSet can be triggered with 0 (unset)
        // We only care about clearing out candidates and
        //  checking for game won in the case where an actual value is set
        if (value > 0) {
            int order = this.game.getLatinSquare().getOrder();
            UserSquare[][] userSquares = this.game.getUserSquares();
            int row = event.getY();
            int col = event.getX();

            // Remove the value that was set from all candidates in row and col.
            // Except for the row it was set on
            for (int i = 0; i < order; i += 1) {
                if (i != row) {
                    userSquares[col][i].removeCandidate(value);
                }

                if (i != col) {
                    userSquares[i][row].removeCandidate(value);
                }
            }

            this.candidatesLayout.SetDisabled();

            if (this.isGameWon()) {
                // Disable stuff
                this.valuesLayout.SetDisabled();
                this.candidatesLayout.SetDisabled();
                this.gameState = GameState.Won;

                this.gameTimer.removeCallbacks(this.updater);
                this.updateTime();

                Date now = new Date();
                long ticks = now.getTime() - this.game.getGameStartTime().getTime();

                this.triggerGameWon(ticks, order);
            }
        } else {
            this.candidatesLayout.SetDisabled(
                    this.getDisabled((UserSquare) event.getSource())
            );
        }
    }

    private Set<Integer> getDisabled(UserSquare square) {
        int order = this.game.getLatinSquare().getOrder();
        int x = square.getX();
        int y = square.getY();
        UserSquare[][] userSquares = this.game.getUserSquares();

        Set<Integer> ret = new HashSet<>();

        for (int i = 0; i < order; i += 1) {
            if (i != y && userSquares[x][i].getValue() != 0) {
                ret.add(userSquares[x][i].getValue());
            }

            if (i != x && userSquares[i][y].getValue() != 0) {
                ret.add(userSquares[i][y].getValue());
            }
        }

        return ret;
    }

    private void setFromSquare() {
        UserSquare currentUserSquare = this.selectedSquare.getUserSquare();

        Set<Integer> disabled = this.getDisabled(currentUserSquare);

        this.valuesLayout.SetDisabled(disabled);
        this.valuesLayout.SetValue(currentUserSquare.getValue());

        if (currentUserSquare.getValue() > 0) {
            this.candidatesLayout.SetDisabled();
        } else {
            this.candidatesLayout.SetDisabled(disabled);
        }
        this.candidatesLayout.SetValues(currentUserSquare.getCandidates());
    }

    /**
     * Sets the game to paused if it is in game.
     */
    public void PauseIfNotPaused() {
        if (this.gameState == GameState.InGame) {
            this.TogglePause();
        }
    }

    /**
     * Toggles the paused state of the game.
     * Do not call if there could be no game at the time.
     */
    public void TogglePause() {
        if (this.gameState == GameState.Paused) {

            // UnPause game
            this.game.resetGameStartTime(this.pausedTime);

            this.gameState = GameState.InGame;
            this.setFromSquare();
            this.updateTime();
            this.gameTimer.postDelayed(this.updater, 1000);

        } else {

            // Pause game
            Date date = new Date();
            this.pausedTime = date.getTime() - this.game.getGameStartTime().getTime();

            this.gameState = GameState.Paused;
            this.candidatesLayout.SetDisabled();
            this.valuesLayout.SetDisabled();
            this.gameTimer.removeCallbacks(this.updater);
            this.timerText.setText(this.getContext().getString(R.string.paused));
        }

        // Redraw since paused state changed
        this.postInvalidate();
    }

    public void Check() {
        // Penalize the game playing time by 15 seconds
        this.game.penalizeGameStartTime(15000);
        this.updateTime();

        LatinSquare latinSquare = this.game.getLatinSquare();
        int[][] values = latinSquare.getValues();
        int order = latinSquare.getOrder();

        for (int i = 0; i < order; i += 1) {
            for (int j = 0; j < order; j += 1) {

                KenKenSquare square = this.uiSquares[i][j];
                int squareValue = square.getUserSquare().getValue();
                if (squareValue > 0 && squareValue != values[i][j]) {
                    square.setMarkedIncorrect(true);
                }
            }
        }

        // Set a timer to clear the UI state change
        this.gameTimer.postDelayed(this.checkClearer, 5000);
    }

    private void initializeGame(int order) {
        this.candidatesLayout.NewGame(order);
        this.valuesLayout.NewGame(order);

        UserSquare[][] userSquares = this.game.getUserSquares();

        this.uiSquares = new KenKenSquare[order][];
        for (int i = 0; i < order; i += 1) {
            this.uiSquares[i] = new KenKenSquare[order];
            for (int j = 0; j < order; j += 1) {
                userSquares[i][j].addValueSetListener(this.valueSetListener);
                this.uiSquares[i][j] = new KenKenSquare(userSquares[i][j]);
                this.uiSquares[i][j].addRequestRedrawListener(this.redrawListener);
            }
        }

        // Pass cage texts into the squares
        List<ICage> cages = this.game.getCages();
        for (ICage cage : cages) {
            Point location = cage.getSignLocation();
            String text = cage.getSignNumber().toString();
            this.uiSquares[location.x][location.y].setCageText(text);
        }

        // Set the first square to be selected
        this.selectedSquare = this.uiSquares[0][0];
        this.selectedSquare.setTouchState(SquareTouchState.Selected);

        this.updateTime();
        this.gameTimer.postDelayed(this.updater, 1000);

        this.gameState = GameState.InGame;

        // Invalidate the drawn canvas
        this.postInvalidate();
    }

    /**
     * Saves out current state of the game.  Returns null if none to save.
     *
     * @return Game JSON or null if there is no game.
     */
    public JSONObject SaveState() {
        if (this.gameState == GameState.Clear || this.gameState == GameState.Won) {
            return null;
        }

        // If we are paused, we want to set the correct time before saving it out.
        if (this.gameState == GameState.Paused) {
            this.game.resetGameStartTime(this.pausedTime);
        }

        JSONObject gameAsJson = this.game.toJson();

        this.PauseIfNotPaused();

        return gameAsJson;
    }

    public void LoadState(JSONObject gameAsJson) {
        this.Clear();
        if (gameAsJson != null) {
            this.game = new KenKenGame(gameAsJson);
            this.initializeGame(this.game.getLatinSquare().getOrder());
            this.TogglePause();
        }
    }

    public void NewGame(int gameSize) {
        this.Clear();
        this.game = new KenKenGame(gameSize);
        this.initializeGame(gameSize);
    }

    public void Clear() {
        this.candidatesLayout.Clear();
        this.valuesLayout.Clear();

        if (this.game != null) {
            // remove event stuff
            for (KenKenSquare[] squares : this.uiSquares) {
                for (KenKenSquare square : squares) {
                    square.clearRequestRedrawListeners();
                }
            }

            for (UserSquare[] row : this.game.getUserSquares()) {
                for (UserSquare square : row) {
                    square.clearValueSetListeners();
                }
            }

            this.gameTimer.removeCallbacks(this.updater);
            this.timerText.setText("");

            this.game = null;
            this.uiSquares = null;
            this.hoverSquare = null;
            this.selectedSquare = null;
        }

        this.gameState = GameState.Clear;

        // Invalidate the drawn canvas
        this.postInvalidate();
    }

    /**
     * Gives the Game Component references to the Candidates, Values, and timer text.
     */
    public void Initialize(
            CandidatesLayout candidatesLayout,
            ValuesLayout valuesLayout,
            TextView timerText) {

        this.candidatesLayout = candidatesLayout;
        this.valuesLayout = valuesLayout;
        this.timerText = timerText;

        this.candidatesLayout.AddCandidateAddedListener(
                event -> GameComponent.this.selectedSquare
                        .getUserSquare()
                        .addCandidate(event.getCandidate())
        );

        this.candidatesLayout.AddCandidateRemovedListener(
                event -> GameComponent.this.selectedSquare
                        .getUserSquare()
                        .removeCandidate(event.getCandidate())
        );

        this.valuesLayout.AddValueChangedListener(
                event -> GameComponent.this.selectedSquare
                        .getUserSquare()
                        .setValue(event.getValue())
        );
    }

    private KenKenSquare getSquareFromPosition(int x, int y) {
        int xIndex = (x - UIConstants.BorderWidth) / this.squareWidthPlusBorder;
        int yIndex = (y - UIConstants.BorderWidth) / this.squareHeightPlusBorder;

        int order = this.game.getLatinSquare().getOrder();
        if (xIndex < 0) {
            xIndex = 0;
        }
        if (yIndex < 0) {
            yIndex = 0;
        }
        if (xIndex >= order) {
            xIndex = order - 1;
        }
        if (yIndex >= order) {
            yIndex = order - 1;
        }

        return this.uiSquares[xIndex][yIndex];
    }

    private void handleDoubleTap() {
        // Set all 1-square cages
        for (ICage cage : this.game.getCages()) {
            List<Point> cageSquares = cage.getSquares();
            if (cageSquares.size() == 1) {
                Point pt = cageSquares.get(0);
                UserSquare square = this.uiSquares[pt.x][pt.y].getUserSquare();
                if (square.getValue() == 0) {
                    square.setValue(cage.getSignNumber().getNumber());
                }
            }
        }

        // Set all values that can only be satisfied by one value
        boolean actionTaken = true;
        while (actionTaken) {
            actionTaken = false;
            for (KenKenSquare[] row : this.uiSquares) {
                for (KenKenSquare uiSquare : row) {
                    UserSquare square = uiSquare.getUserSquare();
                    Set<Integer> disabled = this.getDisabled(square);
                    int order = this.game.getLatinSquare().getOrder();
                    if (square.getValue() == 0 && disabled.size() == order - 1) {
                        int sum = 0;
                        for (int x : disabled) {
                            sum += x;
                        }
                        square.setValue(order * (order + 1) / 2 - sum);
                        actionTaken = true;
                    }
                }
            }
        }

        // After changing values we need to refresh the number pickers
        this.setFromSquare();
    }

    private static final long DOUBLE_TAP_DELTA_MS = 300;
    private long lastTapTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Only want to accept clicks if we are in game.
        if (this.gameState == GameState.InGame) {
            float x = event.getX();
            float y = event.getY();

            KenKenSquare targetSquare =
                    this.getSquareFromPosition((int) x, (int) y);

            // Move, Down are handled as "hovering"
            // Up is handled as the click
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:

                    // If there is a hover square and it is not this one
                    // Then make it not hovered any more.
                    if (this.hoverSquare != null
                            && this.hoverSquare != targetSquare) {
                        this.hoverSquare.setTouchState(SquareTouchState.None);
                    }

                    // If this hovering square is not selected then set
                    //  the hover state on it.
                    if (targetSquare.getTouchState() != SquareTouchState.Selected) {
                        this.hoverSquare = targetSquare;
                        this.hoverSquare.setTouchState(SquareTouchState.Touching);
                    }

                    break;
                case MotionEvent.ACTION_UP:

                    // On mouse up, if there was a hover square then
                    //  clear the hovered state.
                    if (this.hoverSquare != null) {
                        this.hoverSquare.setTouchState(SquareTouchState.None);
                        this.hoverSquare = null;
                    }

                    // Un set the touched state of the previously touched square
                    // This includes removing its event listeners
                    // Then set the selected square and set the touched state
                    //  and rebind the event handler.
                    this.selectedSquare.setTouchState(SquareTouchState.None);
                    this.selectedSquare = targetSquare;
                    this.selectedSquare.setTouchState(SquareTouchState.Selected);

                    // After this, set up the Candidates and Values layouts.
                    this.setFromSquare();

                    long tapTime = System.currentTimeMillis();
                    if (tapTime - lastTapTime < DOUBLE_TAP_DELTA_MS) {
                        this.handleDoubleTap();
                    }
                    lastTapTime = tapTime;

                    break;
            }

            // Return true since we handled the click
            return true;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = GameComponent.defaultSize;
            if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
                width = widthSize;
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = GameComponent.defaultSize;
            if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
                height = heightSize;
            }
        }

        this.setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // If the game is paused, just draw black background and return
        if (this.gameState == GameState.Paused) {
            canvas.drawColor(Color.BLACK);
            return;
        }

        int order = SettingsProvider.GetGameSize();

        int boardWidth = this.getMeasuredWidth();
        int boardHeight = this.getMeasuredHeight();

        // Adjust height / width for borders
        int borders = UIConstants.BorderWidth * (order + 1);
        boardWidth -= borders;
        boardHeight -= borders;

        int squareWidth = boardWidth / order;
        int squareHeight = boardHeight / order;
        this.squareWidthPlusBorder = squareWidth + UIConstants.BorderWidth;
        this.squareHeightPlusBorder = squareHeight + UIConstants.BorderWidth;

        if (this.dimensions == null || this.dimensions.getOrder() != order) {
            this.dimensions =
                    new SquareDrawingDimensions(order, squareWidth, squareHeight);
        }

        int drawnBoardWidth = order * squareWidth + (order + 1) * UIConstants.BorderWidth;
        int drawnBoardHeight = order * squareHeight + (order + 1) * UIConstants.BorderWidth;

        // Draw grids and cages first
        for (int i = 0; i <= order; i += 1) {
            // horizontal grid line
            canvas.drawRect(
                    0, i * (squareHeight + UIConstants.BorderWidth),
                    drawnBoardWidth, i * squareHeight + (i + 1) * UIConstants.BorderWidth,
                    UIConstants.GetGridColor()
            );

            // vertical grid line
            canvas.drawRect(
                    i * (squareWidth + UIConstants.BorderWidth), 0,
                    i * squareWidth + (i + 1) * UIConstants.BorderWidth, drawnBoardHeight,
                    UIConstants.GetGridColor()
            );
        }

        if (this.gameState == GameState.InGame || this.gameState == GameState.Won) {

            // Draw Cages
            for (ICage cage : this.game.getCages()) {
                for (RenderLine line : cage.getRenderLines()) {

                    Point linePosition = line.getPosition();

                    int startX = linePosition.x * (squareWidth + UIConstants.BorderWidth);
                    int startY = linePosition.y * (squareHeight + UIConstants.BorderWidth);

                    int endX = startX;
                    int endY = startY;

                    if (line.getHorizontal()) {
                        endX += line.getLength() * (squareWidth + UIConstants.BorderWidth);
                    } else {
                        endY += line.getLength() * (squareHeight + UIConstants.BorderWidth);
                    }

                    canvas.drawRect(
                            startX, startY,
                            endX + UIConstants.BorderWidth, endY + UIConstants.BorderWidth,
                            UIConstants.GetCageColor()
                    );
                }
            }

            // draw the squares themselves
            for (KenKenSquare[] squares : this.uiSquares) {
                for (KenKenSquare square : squares) {
                    square.drawSquare(canvas, this.dimensions);
                }
            }
        }
    }

    public class GameWonEvent extends EventObject {

        private static final long serialVersionUID = -6896411724437170770L;

        private final long ticks;

        /**
         * Returns the number of ticks that the game took.  This is in ms.
         *
         * @return The number of ticks the game took to win.
         */
        public long getTicks() {
            return this.ticks;
        }

        private final int size;

        /**
         * Returns the size of the game that was won.
         *
         * @return The size of the game that was won.
         */
        public int getSize() {
            return this.size;
        }

        public GameWonEvent(Object sender, long ticks, int size) {
            super(sender);

            this.ticks = ticks;
            this.size = size;
        }
    }

    public interface GameWonListener extends EventListener {
        /**
         * Event handler that is called when a game is won.
         *
         * @param event The event object of the Game Won event.
         */
        void onGameWon(GameWonEvent event);
    }

    private final List<GameWonListener> gameWonListeners =
            new ArrayList<GameWonListener>();

    private void triggerGameWon(long ticks, int size) {
        GameWonEvent event = new GameWonEvent(this, ticks, size);

        for (GameWonListener listener : this.gameWonListeners) {
            listener.onGameWon(event);
        }
    }

    /**
     * Adds the listener to the Game Won listeners.
     *
     * @param listener The Game Won listener to add to the listeners.
     */
    public void AddGameWonListener(GameWonListener listener) {
        this.gameWonListeners.add(listener);
    }

    /**
     * Removes the listener from the Game Won listeners.
     *
     * @param listener The Game Won listener to remove from the listeners.
     */
    public void RemoveGameWonListener(GameWonListener listener) {
        this.gameWonListeners.remove(listener);
    }

    public GameComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.postInvalidate();
    }
}
