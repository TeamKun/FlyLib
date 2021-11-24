package com.flylib3.event.stream

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.event.SimpleFListener
import org.bukkit.event.Event

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
}

class EventStreamStarter<T : Event>(flyLib: FlyLib) : FlyLibComponent(flyLib), EventStreamNode<T, T> {
    val listener = SimpleFListener<T> {
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