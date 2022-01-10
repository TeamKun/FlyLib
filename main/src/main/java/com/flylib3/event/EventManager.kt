package com.flylib3.event

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.event.ex.ExternalEvent
import com.flylib3.event.ex.flylib.FlyLibDefaultExEvents
import com.flylib3.event.stream.EventStreamStarter
import com.flylib3.util.log
import com.flylib3.util.ready
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.RegisteredListener
import kotlin.reflect.KClass

class EventManager(override val flyLib: FlyLib) : FlyLibComponent {
    init {
        ready {
            // Register Default Event
            FlyLibDefaultExEvents(flyLib)
        }
    }


    fun <T : Event> getHandlerList(eventClass: KClass<T>): HandlerList? {
        return try {
            val method = eventClass.java.getDeclaredMethod("getHandlerList")
            method.isAccessible = true
            method.invoke(null) as HandlerList
        } catch (e: NoSuchMethodException) {
            null
        }
    }

    fun registerToHandlerList(
        handlerList: HandlerList,
        executor: EventExecutor,
        listener: Listener,
        priority: EventPriority,
        ignoreCancelled: Boolean
    ) {
        handlerList.register(RegisteredListener(listener, executor, priority, flyLib.plugin, ignoreCancelled))
    }

    fun <T : Event> register(
        listener: SimpleFListener<T>,
        priority: EventPriority = EventPriority.NORMAL
    ): Boolean {
        val handlerList = getHandlerList(listener.eventClass)
        if (handlerList != null) {
            registerToHandlerList(handlerList, listener, listener, priority, false)
            return true
        }
        log("[EventManager] Failed to register listener ${listener.eventClass.simpleName}")
        return false
    }

    /**
     * Call External Event in this plugin.
     */
    fun callEvent(event: ExternalEvent) {
        flyLib.plugin.server.pluginManager.callEvent(event)
    }

    fun <T : Event> stream(clazz: KClass<T>): EventStreamStarter<T> {
        return EventStreamStarter(flyLib, clazz)
    }
}