package com.flylib.flylib3.event.stream

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent
import com.flylib.flylib3.event.SimpleFListener
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import kotlin.reflect.KClass

interface EventStreamNode<From : Event, To : Event> {
    var next: EventStreamNode<To, *>?
    private fun register(next: EventStreamNode<To, *>) {
        this.next = next
    }

    fun execute(event: From)

    fun map(f: (To) -> Event): EventStreamMapOp<To, Event> {
        val node = EventStreamMapOp(f)
        register(node)
        return node
    }

    fun forEach(f: (To) -> Unit) {
        val node = EventStreamForEachOp(f)
        register(node)
    }

    fun filter(f: (To) -> Boolean): EventStreamFilterOp<To> {
        val node = EventStreamFilterOp(f)
        register(node)
        return node
    }

//    fun <T> cancel(): EventStreamCancelOp<T> where T : Cancellable, T : To {
//        val node = EventStreamCancelOp<T>()
//        register(node)
//        return node
//    }
}

class EventStreamStarter<T : Event>(override val flyLib: FlyLib,val eventClass:KClass<T>) : FlyLibComponent, EventStreamNode<T, T> {
    val listener = SimpleFListener<T>(eventClass) {
        execute(it)
    }

    init {
        flyLib.event.register(listener)
    }

    override var next: EventStreamNode<T, *>? = null

    override fun execute(event: T) {
        next?.execute(event)
    }
}

open class EventStreamOperator<From : Event, To : Event> :
    EventStreamNode<From, To> {
    override var next: EventStreamNode<To, *>? = null
    override fun execute(event: From) {
        TODO("To Override")
    }
}

class EventStreamMapOp<From : Event, To : Event>(val mapper: (From) -> To) :
    EventStreamOperator<From, To>() {
    override fun execute(event: From) {
        val e = mapper(event)
        next?.execute(e)
    }
}

class EventStreamFilterOp<From : Event>(val filter: (From) -> Boolean) :
    EventStreamOperator<From, From>() {
    override fun execute(event: From) {
        val e = filter(event)
        if (e && next != null) {
            next!!.execute(event)
        }
    }
}

class EventStreamForEachOp<From : Event>(val f: (From) -> Unit) :
    EventStreamOperator<From, From>() {
    override fun execute(event: From) {
        f(event)
        next?.execute(event)
    }
}

class EventStreamCancelOp<From>() : EventStreamOperator<From, From>() where From : Cancellable, From : Event {
    override fun execute(event: From) {
        event.isCancelled = true
        next?.execute(event)
    }
}