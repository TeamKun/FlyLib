package com.flylib3.event.stream

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.event.SimpleFListener
import org.bukkit.event.Event

interface EventStreamNode<From : Event, To : Event> {
    var next: EventStreamNode<To, *>?
    private fun register(next: EventStreamNode<To, Event>) {
        this.next = next
    }

    private fun register1(next: EventStreamNode<To, Nothing>) {
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
        register1(node)
    }

    fun filter(f: (To) -> To?): EventStreamFilterOp<To> {
        val node = EventStreamFilterOp(f)
        register(node) // TODO なぜかコンパイル通らない(後で考える)
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

class EventStreamFilterOp<From : Event>(val filter: (From) -> From?) :
    EventStreamOperator<From, From>() {
    override fun execute(event: From) {
        val e = filter(event)
        if (e != null && next != null) {
            next!!.execute(e)
        }
    }
}

class EventStreamForEachOp<From : Event>(val f: (From) -> Unit) :
    EventStreamOperator<From, Nothing>() {
    override fun execute(event: From) {
        f(event)
    }
}