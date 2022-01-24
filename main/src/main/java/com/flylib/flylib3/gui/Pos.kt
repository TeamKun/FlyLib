package com.flylib.flylib3.gui;

/**
 * Define position on GUI.
 *
 * @param <X> The X axis type
 * @param <Y> The Y axis type
 */
abstract class Pos<X, Y> {
    abstract fun x(): X
    abstract fun y(): Y
    abstract fun clone(): Pos<X, Y>

    override fun equals(other: Any?): Boolean {
        return if (other is Pos<*, *>) {
            x() == other.x() && y() == other.y()
        } else {
            false
        }
    }
}