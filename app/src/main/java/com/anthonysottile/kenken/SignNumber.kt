package com.anthonysottile.kenken

import org.json.JSONObject

class SignNumber {
    val sign: Sign
    val number: Int

    override fun toString(): String {
        return "${this.number} ${this.sign}"
    }

    constructor(sign: Sign, number: Int) {
        this.sign = sign
        this.number = number
    }

    constructor(json: JSONObject) {
        this.sign = Sign.toSign(json.getInt(SignNumber.signProperty))
        this.number = json.getInt(SignNumber.numberProperty)
    }

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put(SignNumber.signProperty, this.sign.intValue)
        json.put(SignNumber.numberProperty, this.number)
        return json
    }

    companion object {
        private const val signProperty = "Sign"
        private const val numberProperty = "Number"
    }
}
