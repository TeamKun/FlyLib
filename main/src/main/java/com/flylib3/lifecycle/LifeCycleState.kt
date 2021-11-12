package com.flylib3.lifecycle

/**
 * class that define lifecycle of plugin and flylib
 *
 */
class LifeCycleState(val cycleStateName: String) {
    override fun equals(other: Any?): Boolean {
        return if (other is LifeCycleState) {
            other.cycleStateName == cycleStateName
        } else {
            false
        }
    }
}