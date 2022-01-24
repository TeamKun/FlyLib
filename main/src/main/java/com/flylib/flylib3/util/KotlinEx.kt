package com.flylib.flylib3.util

fun <T> List<T>.allIndexed(lambda: (T, Int) -> Boolean): Boolean {
    for ((index, r) in this.withIndex()) {
        if (!lambda(r, index)) return false
    }
    return true
}