package com.anthonysottile.kenken

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class UserSquare {
    val x: Int
    val y: Int
    val candidates: BooleanArray

    var value = 0
        set(x) {
            if (field != x) {
                field = x
                this.triggerValueSetEvent()
                this.triggerChangedEvent()
            }
        }

    private val changedHandlers = ArrayList<(UserSquare) -> Unit>()

    private val valueSetListeners = ArrayList<(UserSquare) -> Unit>()

    fun getCandidatesString(): String {
        val numbers = mutableListOf<Int>()
        for ((i, v) in this.candidates.withIndex()) {
            if (v) {
                numbers.add(i + 1)
            }
        }
        return numbers.joinToString(" ")
    }

    fun addCandidate(value: Int) {
        if (!this.candidates[value - 1]) {
            this.candidates[value - 1] = true
            this.triggerChangedEvent()
        }
    }

    fun removeCandidate(value: Int) {
        if (this.candidates[value - 1]) {
            this.candidates[value - 1] = false
            this.triggerChangedEvent()
        }
    }

    fun addChangedEventHandler(handler: (UserSquare) -> Unit) {
        this.changedHandlers.add(handler)
    }

    private fun triggerChangedEvent() {
        for (listener in this.changedHandlers) {
            listener(this)
        }
    }

    fun addValueSetListener(listener: ((UserSquare) -> Unit)) {
        this.valueSetListeners.add(listener)
    }

    private fun triggerValueSetEvent() {
        for (listener in this.valueSetListeners) {
            listener(this)
        }
    }

    constructor(x: Int, y: Int, order: Int) {
        this.x = x
        this.y = y
        this.candidates = BooleanArray(order)
    }

    fun toJson(): JSONObject {
        val json = JSONObject()

        val candidatesJson = JSONArray()
        for (i in this.candidates.indices) {
            candidatesJson.put(i, this.candidates[i])
        }

        json.put(UserSquare.xProperty, this.x)
        json.put(UserSquare.yProperty, this.y)
        json.put(UserSquare.valueProperty, this.value)
        json.put(UserSquare.candidatesProperty, candidatesJson)

        return json
    }

    constructor(json: JSONObject) {
        this.x = json.getInt(UserSquare.xProperty)
        this.y = json.getInt(UserSquare.yProperty)
        this.value = json.getInt(UserSquare.valueProperty)

        val candidatesJson = json.getJSONArray(UserSquare.candidatesProperty)
        this.candidates = BooleanArray(candidatesJson.length())
        for (i in this.candidates.indices) {
            this.candidates[i] = candidatesJson.getBoolean(i)
        }
    }

    companion object {
        private const val xProperty = "X"
        private const val yProperty = "Y"
        private const val valueProperty = "Value"
        private const val candidatesProperty = "Candidates"
    }
}
