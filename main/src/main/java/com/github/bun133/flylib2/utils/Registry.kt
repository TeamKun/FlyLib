package com.github.bun133.flylib2.utils


class Registry<T> {
    private val list = mutableListOf<Function1<T,Unit>>()

    fun add(t: Function1<T,Unit>) {
        list.add(t)
    }

    fun remove(t: Function1<T,Unit>) {
        list.remove(t)
    }

    fun execute(t: T) {
        list.forEach {
            it(t)
        }
    }
}