package com.flylib.util

class NaturalNumber(var i: Int) {
    operator fun minus(i: Int): Int = this.i - i
    operator fun plus(i: Int): Int = this.i + i
    operator fun times(i: Int): Int = this.i * i
    operator fun div(i: Int): Int = this.i / i
    operator fun rem(i: Int): Int = this.i % i

    operator fun minusAssign(i: Int) {
        this.i -= i
    }

    operator fun plusAssign(i: Int) {
        this.i += i
    }

    operator fun timesAssign(i: Int) {
        this.i *= i
    }

    operator fun divAssign(i: Int) {
        this.i /= i
    }

    operator fun remAssign(i: Int) {
        this.i %= i
    }

    fun toInt(): Int = i
    operator fun compareTo(i: Int): Int {
        return this.i - i
    }


    init {
        if (i < 1) {
            throw NaturalNumberException(i)
        }
    }
}

class NaturalNumberException(i: Int) : Exception("$i is not natural number")