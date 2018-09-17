package com.anthonysottile.kenken

import org.json.JSONArray
import org.json.JSONObject

class LatinSquare {
    val values: Array<IntArray>
    val order: Int

    constructor(order: Int) {
        this.order = order

        // initialize the numbers array
        //  but not the inner arrays. Those get made later.
        this.values = Array(order) { IntArray(order) }

        val picker = NumberPicker(order)

        // Retrieve the base list
        val baseList = IntArray(order)
        for (i in 0 until order) {
            baseList[i] = picker.getNext()
        }

        // Generate the rows
        // Rows is indexed [row][column]
        val rows = Array(order) { IntArray(order) }
        picker.reset()
        for (i in 0 until order) {
            val shift = picker.getNext() - 1
            for (j in 0 until order) {
                // To shift the index correctly
                val index = (j + shift) % order
                rows[i][index] = baseList[j]
            }
        }

        // Rotate the square and "shuffle" rows
        // Columns is indexed [column][row]
        val columns = Array(order) { IntArray(order) }
        for (i in 0 until order) {
            for (j in 0 until order) {
                columns[i][j] = rows[j][i]
            }
        }

        // Swap a column with its next column
        // This needs to be a single swap with a column next to it
        // otherwise the original ordering property is restored :(
        picker.reset()
        val columnIndex = picker.getNext() - 1
        val columnIndexNext = (columnIndex + 1) % order

        val tempColumn = columns[columnIndex]
        columns[columnIndex] = columns[columnIndexNext]
        columns[columnIndexNext] = tempColumn

        // Rotate the square once more and "shuffle" rows
        picker.reset()
        for (i in 0 until order) {
            val row = picker.getNext() - 1
            for (j in 0 until order) {
                this.values[i][j] = columns[j][row]
            }
        }
    }

    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put(LatinSquare.orderProperty, this.order)

        val outerArray = JSONArray()
        for (row in this.values) {
            val innerArray = JSONArray()
            for (x in row) {
                innerArray.put(x)
            }
            outerArray.put(innerArray)
        }

        json.put(LatinSquare.valuesProperty, outerArray)

        return json
    }

    constructor(json: JSONObject) {
        this.order = json.getInt(LatinSquare.orderProperty)

        val outerArray = json.getJSONArray(LatinSquare.valuesProperty)

        this.values = Array(this.order) { IntArray(this.order) }
        for (i in this.values.indices) {
            val innerArray = outerArray.getJSONArray(i)
            for (j in this.values[i].indices) {
                this.values[i][j] = innerArray.getInt(j)
            }
        }
    }

    companion object {
        private const val orderProperty = "Order"
        private const val valuesProperty = "Values"
    }
}
