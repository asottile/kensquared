package com.anthonysottile.kenken.settings

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class GameStatistics {
    var gameSize = 0
        private set

    var gamesPlayed = 0
        private set

    var gamesWon = 0
        private set

    var totalSeconds = 0
        private set

    var bestTime = Integer.MAX_VALUE
        private set

    var bestTimeDate: Date? = null
        private set

    fun gameStarted() {
        this.gamesPlayed += 1
    }

    fun gameWon(time: Int): Boolean {
        this.gamesWon += 1
        this.totalSeconds += time

        var highScore = false
        if (time < this.bestTime) {
            this.bestTime = time
            this.bestTimeDate = Date()
            highScore = true
        }

        return highScore
    }

    constructor(gameSize: Int) {
        this.gameSize = gameSize
        this.gamesPlayed = 0
        this.gamesWon = 0
        this.totalSeconds = 0
        this.bestTime = Integer.MAX_VALUE
        this.bestTimeDate = null
    }

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put(GameSize, this.gameSize)
        json.put(GamesPlayed, this.gamesPlayed)
        json.put(GamesWon, this.gamesWon)
        json.put(TotalSeconds, this.totalSeconds)

        if (this.bestTimeDate != null) {
            json.put(BestTime, this.bestTime)
            json.put(
                    BestTimeDate,
                    dateFormatter.format(
                            this.bestTimeDate!!
                    )
            )
        }

        return json
    }

    constructor(json: JSONObject) {
        this.gameSize = json.getInt(GameSize)
        this.gamesPlayed = json.getInt(GamesPlayed)
        this.gamesWon = json.getInt(GamesWon)
        this.totalSeconds = json.getInt(TotalSeconds)

        if (json.has(BestTimeDate)) {
            this.bestTime = json.getInt(BestTime)
            this.bestTimeDate = dateFormatter.parse(
                    json.getString(BestTimeDate)
            )
        }
    }

    companion object {
        // NOTE: According to http://developer.android.com/reference/java/util/Locale.html
        //        Locale.US should be used like C#  CultureInfo.InvariantCulture
        private val dateFormatter = SimpleDateFormat.getDateInstance(
                SimpleDateFormat.LONG,
                Locale.US
        )

        private const val GameSize = "GameSize"
        private const val GamesPlayed = "GamesPlayed"
        private const val GamesWon = "GamesWon"
        private const val TotalSeconds = "TotalSeconds"
        private const val BestTime = "BestTime"
        private const val BestTimeDate = "BestTimeDate"
    }
}
