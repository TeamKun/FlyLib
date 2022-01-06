package com.flylib3.task

import com.flylib3.FlyLib
import com.flylib3.event.SimpleFListener
import org.bukkit.event.Event
import kotlin.reflect.KClass

class EventRunnableStarter<T : Event, R>(
    val event: KClass<T>, override val flyLib: FlyLib,
    val l: FRunnableContext.(T) -> R
) : FRunnableStarter<T, R>(flyLib, l) {
    val listener = SimpleFListener<T>(event) { run(it, null) }.also { flyLib.event.register(it) }

    override fun run() {
        // Nothing
    }

    override fun run(event: T, context: FRunnableContext?): R? {
        if (l == null) return null
        val ctx = createContext()
        val r = l.invoke(ctx, event)
        if (!ctx.isMarkedBreakThen) {
            processThen(r, ctx)
        }
        return r
    }
}