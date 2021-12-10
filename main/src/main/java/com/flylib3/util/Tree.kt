package com.flylib3.util

import org.bukkit.Warning

/**
 * Class define Tree-Graph Start Point
 */
class Tree<T> : TreeEntry<T>() {
    fun findAll(child: TreeEntry<T>): MutableList<TreeEntry<T>> {
        return findAll { it == child }
    }

    fun findValueAll(value: T): MutableList<T> {
        return findValueAll { it == value }
    }

    fun findAll(lambda: (TreeEntry<T>) -> Boolean): MutableList<TreeEntry<T>> {
        return find(lambda)
    }

    fun findValueAll(lambda: (T) -> Boolean): MutableList<T> {
        return findValue(lambda)
    }
}

open class TreeEntry<T>() {
    private fun append(child: TreeEntry<T>) {
        this.childField.add(child)
        updateState()
    }

    fun append(vararg child: TreeEntry<T>): TreeEntry<T> {
        child.forEach { append(it) }
        return this
    }

    private fun remove(child: TreeEntry<T>) {
        this.childField.remove(child)
        updateState()
    }

    fun remove(vararg child: TreeEntry<T>): TreeEntry<T> {
        child.forEach { remove(it) }
        return this
    }

    private fun append(value: T) {
        this.valueField.add(value)
        updateState()
    }

    fun append(vararg value: T): TreeEntry<T> {
        value.forEach { append(it) }
        return this
    }

    private fun remove(value: T) {
        this.valueField.remove(value)
        updateState()
    }

    fun remove(vararg value: T): TreeEntry<T> {
        value.forEach { remove(it) }
        return this
    }

    private fun updateState() {
        val valueSize = valueField.size
        val childSize = childField.size

        state = if (valueSize == 0 && childSize == 0) {
            TreeEntryType.NONE
        } else if (valueSize != 0 && childSize == 0) {
            TreeEntryType.VALUE
        } else if (valueSize == 0 && childSize != 0) {
            TreeEntryType.CHILDREN
        } else {
            TreeEntryType.CHILDREN_AND_VALUE
        }
    }

    private val valueField = mutableListOf<T>()
    private val childField = mutableListOf<TreeEntry<T>>()
    var state = TreeEntryType.NONE
        private set

    fun values() = valueField.toMutableList()
    fun children() = childField.toMutableList()

    fun isChild() = state.isChild()
    fun isValue() = state.isValue()

    fun find(child: TreeEntry<T>): MutableList<TreeEntry<T>> {
//        if (child == this) return this
//        if (isChild()) {
//            return this.children().firstOrNull { it.find(child) != null }
//        } else {
//            return null
//        }
        return find { it == child }
    }

    fun find(p: (TreeEntry<T>) -> Boolean): MutableList<TreeEntry<T>> {
        val l = if (isChild()) {
            this.children().map { it.find(p) }.flatten().toMutableList()
        } else {
            mutableListOf()
        }
        if (p(this)) {
            l.add(this)
        }
        return l
    }

    fun findValue(p: (T) -> Boolean): MutableList<T> {
        val l = if (isChild()) {
            this.children().map { it.findValue(p) }.flatten().toMutableList()
        } else {
            mutableListOf()
        }
        if (this.isValue()) {
            l.addAll(this.values().filter(p))
        }
        return l
    }

    fun find(value: T) {
        if (isValue()) {
            if (this.values().contains(value))
                return
        }
    }

    fun all(f: (T) -> Unit) {
        if (isChild()) {
            this.children().forEach {
                it.all(f)
            }
        }

        if (isValue()) {
            this.values().forEach(f)
        }
    }
}

enum class TreeEntryType {
    NONE, // This TreeEntry has nothing
    CHILDREN, // This TreeEntry has children only
    VALUE, // This TreeEntry has values only
    CHILDREN_AND_VALUE; // This TreeEntry has both of children and values

    fun isValue(): Boolean {
        return this == VALUE || this == CHILDREN_AND_VALUE
    }

    fun isChild(): Boolean {
        return this == CHILDREN || this == CHILDREN_AND_VALUE
    }
}