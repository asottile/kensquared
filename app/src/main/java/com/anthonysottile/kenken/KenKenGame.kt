package com.anthonysottile.kenken

import android.graphics.Point
import com.anthonysottile.kenken.cages.BaseCage
import com.anthonysottile.kenken.cages.CageGenerator
import com.anthonysottile.kenken.cages.ICage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class KenKenGame {

    // Update this as squares obtain values
    var squaresWithValues = 0
        private set

    var gameStartTime: Date
    val latinSquare: LatinSquare

    var userSquares: Array<Array<UserSquare>>
    private val cageSquareOccupied: Array<BooleanArray>

    private var cages: MutableList<ICage>

    private fun valueSetListener(square: UserSquare) {
        if (square.value > 0) {
            this@KenKenGame.squaresWithValues += 1
        } else {
            this@KenKenGame.squaresWithValues -= 1
        }
    }

    /**
     * Resets the game time to the specified milliseconds.
     *
     * @param milliseconds The amount of elapsed time to set the game time
     * to.
     */
    fun resetGameStartTime(milliseconds: Long) {
        val date = Date()
        date.time = date.time - milliseconds
        this.gameStartTime = date
    }

    fun penalizeGameStartTime(milliseconds: Long) {
        this.gameStartTime.time = this.gameStartTime.time - milliseconds
    }

    fun getCages(): List<ICage> {
        return this.cages
    }

    private fun squareIsOffBoard(p: Point): Boolean {
        val order = this.latinSquare.order

        return p.x >= order ||
                p.y >= order ||
                p.x < 0 ||
                p.y < 0
    }

    fun squareIsValid(p: Point): Boolean {
        return !this.squareIsOffBoard(p) && !this.cageSquareOccupied[p.x][p.y]
    }

    fun setOccupied(p: Point) {
        this.cageSquareOccupied[p.x][p.y] = true
    }

    private fun postInitialize() {
        // For shared "constructor" code

        // We are going to attach to the value set event on our user squares to
        //  make sure they have a value when being selected.  This way we can count
        //  the number of squares the user has filled in and allow for a faster
        //  calculation of the winning condition.

        for (row in this.userSquares) {
            for (square in row) {
                square.addValueSetListener(this::valueSetListener)
            }
        }
    }

    constructor(order: Int) {
        this.latinSquare = LatinSquare(order)

        this.cageSquareOccupied = Array(order) { BooleanArray(order) }
        this.userSquares = Array(order) { i ->
            Array(order) { j -> UserSquare(i, j, order) }
        }

        this.cages = ArrayList()

        CageGenerator.Generate(this)

        this.gameStartTime = Date()

        this.postInitialize()
    }

    fun toJson(): JSONObject {
        val json = JSONObject()

        val now = Date()
        val timeElapsed = now.time - this.gameStartTime.time

        val cagesJson = JSONArray()
        for ((i, cage) in this.cages.withIndex()) {
            cagesJson.put(i, cage.ToJson())
        }

        val userSquaresJson = JSONArray()
        for ((i, row) in this.userSquares.withIndex()) {
            val innerArray = JSONArray()

            for ((j, userSquare) in row.withIndex()) {
                innerArray.put(j, userSquare.toJson())
            }

            userSquaresJson.put(i, innerArray)
        }

        json.put(KenKenGame.squaresWithValuesProperty, this.squaresWithValues)
        json.put(KenKenGame.gameTimeElapsedProperty, timeElapsed)
        json.put(KenKenGame.latinSquareProperty, this.latinSquare.toJson())
        json.put(KenKenGame.cagesProperty, cagesJson)
        json.put(KenKenGame.userSquaresProperty, userSquaresJson)

        return json
    }

    constructor(json: JSONObject) {
        this.squaresWithValues = json.getInt(KenKenGame.squaresWithValuesProperty)

        val timeElapsed = json.getLong(KenKenGame.gameTimeElapsedProperty)
        this.gameStartTime = Date()
        this.gameStartTime.time = this.gameStartTime.time - timeElapsed

        this.latinSquare = LatinSquare(json.getJSONObject(KenKenGame.latinSquareProperty))
        this.cageSquareOccupied = Array(this.latinSquare.order) { BooleanArray(this.latinSquare.order) }

        val cagesJson = json.getJSONArray(KenKenGame.cagesProperty)
        this.cages = ArrayList()
        for (i in 0 until cagesJson.length()) {
            this.cages.add(BaseCage.ToCage(cagesJson.getJSONObject(i)))
        }

        val userSquareJson = json.getJSONArray(KenKenGame.userSquaresProperty)
        val order = userSquareJson.length()
        this.userSquares = Array(order) { i ->
            val inner = userSquareJson.getJSONArray(i)
            return@Array Array(order) { j -> UserSquare(inner.getJSONObject(j)) }
        }

        this.postInitialize()
    }

    companion object {
        private const val squaresWithValuesProperty = "SquaresWithValues"
        private const val gameTimeElapsedProperty = "GameTimeElapsed"
        private const val latinSquareProperty = "LatinSquare"
        private const val cagesProperty = "Cages"
        private const val userSquaresProperty = "UserSquares"
    }
}
