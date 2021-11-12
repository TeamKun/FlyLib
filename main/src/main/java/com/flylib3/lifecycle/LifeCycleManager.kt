package com.flylib3.lifecycle

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent

class LifeCycleManager(flyLib: FlyLib) : FlyLibComponent(flyLib) {
    var now: LifeCycleState = LifeCycles.Init.lifeCycle()
        private set

    fun set(state: LifeCycleState) {
        now = state
    }

    fun onEnable() {
        set(LifeCycles.Init.lifeCycle())
    }

    fun onWait() {
        set(LifeCycles.Wait.lifeCycle())
    }

    fun onPrepare() {
        set(LifeCycles.Prepare.lifeCycle())
    }

    fun onAction() {
        set(LifeCycles.Action.lifeCycle())
    }

    fun onDisable() {
        set(LifeCycles.Disposed.lifeCycle())
    }
}