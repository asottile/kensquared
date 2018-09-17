package com.anthonysottile.kenken

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class UserSquare {
    private val rowValues: MutableSet<Int>
    private val colValues: MutableSet<Int>
    val candidates: MutableSet<Int> = TreeSet()

    var value = 0
        set(x) {
            if (field != x) {
                if (field == 0) {
                    this.rowValues.add(x)
                    this.colValues.add(x)
                } else {
                    this.rowValues.remove(field)
                    this.colValues.remove(field)
                }

                field = x
                this.triggerValueSetEvent()
                this.triggerChangedEvent()
            }
        }

    private val changedHandlers = ArrayList<(UserSquare) -> Unit>()
    private val valueSetListeners = ArrayList<(UserSquare) -> Unit>()

    fun getCandidatesString(): String {
        val ret = this.candidates - this.rowValues - this.colValues
        return ret.joinToString(" ")
    }

    fun addCandidate(value: Int) {
        this.candidates.add(value)
        this.triggerChangedEvent()
    }

    fun removeCandidate(value: Int) {
        this.candidates.remove(value)
        this.triggerChangedEvent()
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
        this.valueSetListeners.forEach { it(this) }
    }

    constructor(rowValues: MutableSet<Int>, colValues: MutableSet<Int>) {
        this.rowValues = rowValues
        this.colValues = colValues
    }

    fun toJson(): JSONObject {
        val ret = JSONObject()

        val candidatesJson = JSONArray()
        this.candidates.forEach { candidatesJson.put(it) }

        ret.put(UserSquare.valueProperty, this.value)
        ret.put(UserSquare.candidatesProperty, candidatesJson)
        return ret
    }

    constructor(json: JSONObject, rowValues: MutableSet<Int>,  colValues: MutableSet<Int>) {
        this.rowValues = rowValues
        this.colValues = colValues
        this.value = json.getInt(UserSquare.valueProperty)

        val candidatesJson = json.getJSONArray(UserSquare.candidatesProperty)
        for (i in 0 until candidatesJson.length()) {
            this.candidates.add(candidatesJson.getInt(i))
        }
    }

    companion object {
        private const val valueProperty = "Value"
        private const val candidatesProperty = "CandidatesV2"
    }
}
