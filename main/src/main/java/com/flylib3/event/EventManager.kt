package com.flylib3.event

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent

class EventManager(flyLib: FlyLib) : FlyLibComponent(flyLib) {
    fun register(listener: FListener<*>) {
        flyLib.plugin.server.pluginManager.registerEvents(listener, flyLib.plugin)
    }

    fun registerExternalEvent() {

    }
}