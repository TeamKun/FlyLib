package com.flylib3.event

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

interface FListener<T : Event> : Listener {
    @EventHandler
    fun onEvent(t: T)
}

class SimpleFListener<T : Event>(val f: (T) -> Unit) : FListener<T> {
    override fun onEvent(t: T) = f(t)
}