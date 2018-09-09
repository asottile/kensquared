package com.anthonysottile.kenken

import java.util.*

class NumberPicker(private val length: Int) {
    private var attempted = 0
    private val collection = IntArray(length)

    fun getRemaining(): Int {
        return this.length - this.attempted
    }

    fun getNext(): Int {
        if (this.attempted == this.length) {
            return -1
        }

        val lastCardIndex = this.length - this.attempted - 1

        val drawn = NumberPicker.random.nextInt(this.length - this.attempted)

        // swap the drawn number with the last
        val temp = this.collection[drawn]
        this.collection[drawn] = this.collection[lastCardIndex]
        this.collection[lastCardIndex] = temp

        this.attempted++

        return temp
    }

    fun reset() {
        this.attempted = 0
    }

    init {
        for (i in this.collection.indices) {
            this.collection[i] = i + 1
        }
    }

    companion object {
        private val random = Random()
    }
}
