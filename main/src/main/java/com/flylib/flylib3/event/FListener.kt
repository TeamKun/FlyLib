package com.flylib.flylib3.event

import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import kotlin.reflect.KClass


class SimpleFListener<T : Event>(val eventClass: KClass<T>, val f: (T) -> Unit) : EventExecutor, Listener {
    fun event(t: T) {
        f(t)
    }

    override fun execute(listener: Listener, event: Event) {
        if (eventClass.isInstance(event)) {
            event(eventClass.java.cast(event))
        }
    }
}