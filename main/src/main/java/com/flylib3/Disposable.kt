package com.flylib3

interface Disposable<T> {
    fun dispose(t: T)
}