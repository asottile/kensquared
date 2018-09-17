package com.anthonysottile.kenken

import android.graphics.Point
import com.anthonysottile.kenken.cages.BaseCage
import com.anthonysottile.kenken.cages.CageGenerator
import com.anthonysottile.kenken.cages.ICage
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class KenKenGame {
    var squaresWithValues = 0
        private set

    var gameStartTime: Date = Date()
    val latinSquare: LatinSquare

    val rowValues: Array<MutableSet<Int>>
    val colValues: Array<MutableSet<Int>>

    var userSquares: Array<Array<UserSquare>>
    private val cageSquareOccupied: Array<BooleanArray>

    val cages: MutableList<ICage> = ArrayList()

    private fun valueSetListener(square: UserSquare) {
        if (square.value > 0) {
            this.squaresWithValues += 1
        } else {
            this.squaresWithValues -= 1
        }
    }

    fun penalizeGameStartTime(milliseconds: Long) {
        this.gameStartTime.time -= milliseconds
    }

    fun resetGameStartTime(milliseconds: Long) {
        this.gameStartTime = Date()
        this.penalizeGameStartTime(milliseconds)
    }

    private fun squareIsOffBoard(p: Point): Boolean {
        val order = this.latinSquare.order

        return p.x >= order || p.y >= order || p.x < 0 || p.y < 0
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

        this.rowValues = Array(order) { TreeSet<Int>() }
        this.colValues = Array(order) { TreeSet<Int>() }

        this.cageSquareOccupied = Array(order) { BooleanArray(order) }
        this.userSquares = Array(order) { i ->
            Array(order) { j -> UserSquare(this.rowValues[i], this.colValues[j]) }
        }

        CageGenerator.Generate(this)

        this.postInitialize()
    }

    fun toJson(): JSONObject {
        val json = JSONObject()

        val timeElapsed = Date().time - this.gameStartTime.time

        val cagesJson = JSONArray()
        this.cages.forEach { cagesJson.put(it.ToJson()) }

        val userSquaresJson = JSONArray()
        for (row in this.userSquares) {
            val innerArray = JSONArray()
            for (square in row) {
                innerArray.put(square.toJson())
            }
            userSquaresJson.put(innerArray)
        }

        json.put(KenKenGame.gameTimeElapsedProperty, timeElapsed)
        json.put(KenKenGame.latinSquareProperty, this.latinSquare.toJson())
        json.put(KenKenGame.cagesProperty, cagesJson)
        json.put(KenKenGame.userSquaresProperty, userSquaresJson)

        return json
    }

    constructor(json: JSONObject) {
        val elapsed = json.getLong(KenKenGame.gameTimeElapsedProperty)
        this.resetGameStartTime(elapsed)

        this.latinSquare = LatinSquare(json.getJSONObject(KenKenGame.latinSquareProperty))
        this.cageSquareOccupied = Array(this.latinSquare.order) { BooleanArray(this.latinSquare.order) }

        val cagesJson = json.getJSONArray(KenKenGame.cagesProperty)
        for (i in 0 until cagesJson.length()) {
            this.cages.add(BaseCage.ToCage(cagesJson.getJSONObject(i)))
        }

        this.rowValues = Array(this.latinSquare.order) { TreeSet<Int>() }
        this.colValues = Array(this.latinSquare.order) { TreeSet<Int>() }

        val userSquareJson = json.getJSONArray(KenKenGame.userSquaresProperty)
        this.userSquares = Array(this.latinSquare.order) { i ->
            val inner = userSquareJson.getJSONArray(i)
            return@Array Array(this.latinSquare.order) { j ->
                UserSquare(
                        inner.getJSONObject(j),
                        this.rowValues[i],
                        this.colValues[j]
                )
            }
        }

        for ((i, row) in this.userSquares.withIndex()) {
            for ((j, square) in row.withIndex()) {
                if (square.value != 0) {
                    this.squaresWithValues += 1
                    this.rowValues[i].add(square.value)
                    this.colValues[j].add(square.value)
                }
            }
        }

        this.postInitialize()
    }

    companion object {
        private const val gameTimeElapsedProperty = "GameTimeElapsed"
        private const val latinSquareProperty = "LatinSquare"
        private const val cagesProperty = "Cages"
        private const val userSquaresProperty = "UserSquares"
    }
}
