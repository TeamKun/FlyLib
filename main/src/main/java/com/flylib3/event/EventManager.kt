package com.flylib3.event

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.event.ex.ExternalEvent
import com.flylib3.event.stream.EventStreamStarter
import org.bukkit.event.Event
import kotlin.reflect.KClass

class EventManager(override val flyLib: FlyLib) : FlyLibComponent {
    fun register(listener: FListener<*>) {
        flyLib.plugin.server.pluginManager.registerEvents(listener, flyLib.plugin)
    }

    /**
     * Call External Event in this plugin.
     */
    fun callEvent(event: ExternalEvent) {
        flyLib.plugin.server.pluginManager.callEvent(event)
    }

    fun <T : Event> stream(clazz: KClass<T>): EventStreamStarter<T> {
        return EventStreamStarter(flyLib)
    }
}