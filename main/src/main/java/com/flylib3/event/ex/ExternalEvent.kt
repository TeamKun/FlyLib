package com.flylib3.event.ex

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Class base of all External Events
 */
open class ExternalEvent(isAsync: Boolean = false) : Event(isAsync) {
    val handlerList = HandlerList()
    override fun getHandlers() = handlerList
}