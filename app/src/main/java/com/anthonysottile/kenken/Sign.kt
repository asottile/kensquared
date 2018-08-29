package com.anthonysottile.kenken

enum class Sign(val intValue: Int, private val s: String) {

    Add(0, "+"),
    Subtract(1, "-"),
    Multiply(2, "×"),
    Divide(3, "÷"),
    None(4, "");

    override fun toString(): String {
        return this.s
    }

    companion object {
        private val signsByInt = arrayOf(Sign.Add, Sign.Subtract, Sign.Multiply, Sign.Divide, Sign.None)

        /**
         * Converts the number passed in to the enumeration type.
         *
         * @param n The number to convert to the Sign enum.  Must be inclusively between
         * [Sign.Add.getIntValue(), Sign.None.getIntValue()]
         * @return Returns the Sign enum from the integer.
         */
        fun toSign(n: Int): Sign {
            return Sign.signsByInt[n]
        }
    }
}
