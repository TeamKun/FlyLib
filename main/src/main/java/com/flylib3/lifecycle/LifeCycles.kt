package com.flylib3.lifecycle

/**
 * Default Lifecycles
 *
 * Init - Initializing the Plugin or FlyLib.
 * Wait - Init is end and waiting for next action.
 * Prepare(1) - Preparing for action.
 * Prepare(2) - Action is suspended and the Plugin and FlyLib is ready for next action.
 * Action - The Plugin and FlyLib is now under action.
 * Disposed - The Plugin and FlyLib is Disposed
 */
enum class LifeCycles(private val expectedIndex: Int) {
    Init(1), Wait(2), Prepare(3), Action(4), Disposed(0);

    fun expected(): LifeCycles {
        return values()[expectedIndex]
    }

    fun lifeCycle(): LifeCycleState {
        return LifeCycleState(name)
    }
}