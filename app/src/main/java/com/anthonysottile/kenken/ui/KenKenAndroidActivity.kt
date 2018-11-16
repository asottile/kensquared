package com.anthonysottile.kenken.ui

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider
import com.anthonysottile.kenken.settings.StatisticsManager
import com.anthonysottile.kenken.ui.GameComponent.GameState

import org.json.JSONException
import org.json.JSONObject
import android.app.FragmentManager



class KenKenAndroidActivity : Activity() {

    private lateinit var gameComponent: GameComponent

    private var gameWonNewHighScore = false
    private var gameWonTicks: Long = -1

    private fun gameWon(ticks: Long, size: Int) {
        this@KenKenAndroidActivity.gameWonNewHighScore = StatisticsManager.gameEnded(
                size,
                SettingsProvider.hardMode,
                ticks
        )

        this@KenKenAndroidActivity.gameWonTicks = ticks

        this@KenKenAndroidActivity.showDialog(
                KenKenAndroidActivity.GameWonDialogId
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Give a reference to settings to our static settings manager
        // Also attach to the settings's event handler
        val preferences = this.getSharedPreferences(KenKenAndroidActivity.preferences, 0)
        SettingsProvider.initialize(preferences)
        StatisticsManager.initialize(preferences)
        SettingsProvider.addGameSizeChangedListener { this@KenKenAndroidActivity.gameComponent.clear() }
        SettingsProvider.addHardModeChangedListener { this@KenKenAndroidActivity.gameComponent.clear() }

        // Give a reference to resources for Bitmap cache
        BitmapCache.initialize(this.resources)

        this.setContentView(R.layout.main)

        this.gameComponent = this.findViewById(R.id.gameComponent) as GameComponent
        val candidatesLayout = this.findViewById(R.id.candidatesLayout) as CandidatesLayout
        val valuesLayout = this.findViewById(R.id.valuesLayout) as ValuesLayout
        val timerText = this.findViewById(R.id.timerText) as TextView

        // Give the gameComponent references to layouts.
        this.gameComponent.initialize(
                candidatesLayout,
                valuesLayout,
                timerText
        )

        this.gameComponent.addGameWonListener(this::gameWon)
    }

    override fun onPause() {
        super.onPause()

        // Pause the game since they are navigating away
        this.gameComponent.pauseIfNotPaused()
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore the saved state if applicable
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KenKenAndroidActivity.saveGameBundleProperty)) {
                val gameJsonString = savedInstanceState.getString(KenKenAndroidActivity.saveGameBundleProperty)

                if (gameJsonString.isNotEmpty()) {
                    try {
                        val gameAsJson = JSONObject(gameJsonString)
                        this.gameComponent.loadState(gameAsJson)
                    } catch (e: JSONException) {
                    }
                }
            }
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        val game = this.gameComponent.saveState()
        if (game != null) {
            savedInstanceState.putString(
                    KenKenAndroidActivity.saveGameBundleProperty,
                    game.toString()
            )
        }
    }

    private fun newGame() {
        val gameSize = SettingsProvider.gameSize
        StatisticsManager.gameStarted(gameSize, SettingsProvider.hardMode)
        this.gameComponent.newGame(gameSize)
    }

    private fun pauseGame() {
        this.gameComponent.togglePause()
    }

    private fun checkGame() {
        this.gameComponent.check()
    }

    private fun showPreferences() {
        this.gameComponent.pauseIfNotPaused()
        PreferencesDialog().show(this.fragmentManager, null)
    }

    private fun showStatistics() {
        this.gameComponent.pauseIfNotPaused()
        StatisticsDialog().show(this.fragmentManager, null)
    }

    private fun showAbout() {
        this.gameComponent.pauseIfNotPaused()
        AboutDialog().show(this.fragmentManager, null)
    }

    override fun onCreateDialog(id: Int): Dialog? {
        return when (id) {
            GameWonDialogId -> GameWonDialog(this)
            else -> null
        }
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        when (id) {
            KenKenAndroidActivity.GameWonDialogId ->
                (dialog as GameWonDialog).setup(
                        SettingsProvider.gameSize,
                        SettingsProvider.hardMode,
                        this.gameWonNewHighScore,
                        this.gameWonTicks
                )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val gameState = this.gameComponent.gameState
        menu.findItem(R.id.check).isEnabled = gameState === GameState.InGame
        menu.findItem(R.id.pause).isEnabled = gameState === GameState.InGame || gameState === GameState.Paused
        if (gameState !== GameState.Paused) {
            menu.findItem(R.id.pause).setTitle(R.string.pause)
        } else {
            menu.findItem(R.id.pause).setTitle(R.string.resume)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.newGame -> {
                this.newGame()
                return true
            }
            R.id.pause -> {
                this.pauseGame()
                return true
            }
            R.id.check -> {
                this.checkGame()
                return true
            }
            R.id.preferences -> {
                this.showPreferences()
                return true
            }
            R.id.statistics -> {
                this.showStatistics()
                return true
            }
            R.id.about -> {
                this.showAbout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val GameWonDialogId = 2

        private const val preferences = "com.anthonysottile.kenken"
        private const val saveGameBundleProperty = "SavedGame"
    }
}
