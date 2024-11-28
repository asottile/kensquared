package com.anthonysottile.kenken.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import com.anthonysottile.kenken.R
import com.anthonysottile.kenken.settings.SettingsProvider
import com.anthonysottile.kenken.settings.StatisticsManager
import com.anthonysottile.kenken.ui.GameComponent.GameState
import org.json.JSONException
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream


class KenKenAndroidActivity : Activity() {
    private lateinit var gameComponent: GameComponent

    private fun gameWon(ticks: Long, size: Int) {
        val highScore = StatisticsManager.gameEnded(
                size,
                SettingsProvider.hardMode,
                ticks
        )

        GameWonDialog().setup(highScore, ticks, this).show(this.fragmentManager, null)
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

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore the saved state if applicable
        if (savedInstanceState.containsKey(KenKenAndroidActivity.saveGameBundleProperty)) {
            val gameJsonString = savedInstanceState.getString(KenKenAndroidActivity.saveGameBundleProperty)

            if (gameJsonString != null) {
                if (gameJsonString.isNotEmpty()) {
                    try {
                        val gameAsJson = JSONObject(gameJsonString)
                        this.gameComponent.loadState(gameAsJson)
                    } catch (_: JSONException) {
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

    fun newGame() {
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
            R.id.exportStatistics -> {
                this.gameComponent.pauseIfNotPaused()
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "kensquared-statistics.json")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, requestExportStatistics)
                }
                return true
            }
            R.id.importStatistics -> {
                this.gameComponent.pauseIfNotPaused()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "kensquared-statistics.json")
                }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, requestImportStatistics)
                }
                return true
            }
            R.id.about -> {
                this.showAbout()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        if (requestCode == requestExportStatistics && resultCode == Activity.RESULT_OK) {
            this.contentResolver.openFileDescriptor(data.data!!, "w").use {
                FileOutputStream(it!!.fileDescriptor).use {
                    it.write(StatisticsManager.exportStatistics().toByteArray())
                }
            }
        } else if (requestCode == requestImportStatistics && resultCode == Activity.RESULT_OK) {
            this.contentResolver.openFileDescriptor(data.data!!, "r").use {
                FileInputStream(it!!.fileDescriptor).use {
                    if (StatisticsManager.importStatistics(it.bufferedReader().readText())) {
                        this.gameComponent.clear()
                        Toast.makeText(this, "statistics imported!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "could not import statistics", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val requestExportStatistics = 1
        private const val requestImportStatistics = 2
        private const val preferences = "com.anthonysottile.kenken"
        private const val saveGameBundleProperty = "SavedGame"
    }
}
