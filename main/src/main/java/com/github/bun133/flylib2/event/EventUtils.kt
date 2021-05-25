package com.github.bun133.flylib2.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerEvent
import org.bukkit.plugin.Plugin

/**
 * 何かで時間内のEventをすべてCancelしたくなることあるはず。
 * @param isCancel Eventをキャンセルするかしないか
 */
class EventProxy<T : Event>(val plugin: Plugin, val receiver: (T) -> Unit = {}, val isCancel: Boolean = true) :
    Listener {
    private var inited = false
    private var isGoing = false
    fun start() {
        if (!inited) plugin.server.pluginManager.registerEvents(this, plugin)
        isGoing = true
    }

    fun end() {
        isGoing = false
    }

    @EventHandler
    fun onEvent(e: T) {
        if (e is Cancellable) {
            if (isGoing) {
                e.isCancelled = isCancel
                receiver(e)
            }
        } else {
            throw Exception("Event Proxy Can't Receive Event that cannot be canceled")
        }
    }
}

/**
 * 一人一つずつEventを集めたいとかあるよね。
 * @param isCancel Eventをキャンセルするかしないか
 */
class EventCollector<T : PlayerEvent>(
    val plugin: Plugin,
    val mode: CollectMode = CollectMode.All,
    val isCancel: Boolean = true
) {
    enum class CollectMode {
        // 最初の一つを集めてくる(上書き無し)
        FirstOne,

        // 最後の一つを集めてくる(上書き有り)
        FinalOne,

        // 全部持ってこい!!!!(止めずにCollectしまくらないよう注意)
        All,
    }

    private val proxy = EventProxy<T>(plugin, { t -> onEvent(t) }, isCancel = isCancel)
    private var map = mutableMapOf<Player, MutableList<T>>()

    private fun onEvent(t: T) {
        when (mode) {
            CollectMode.All -> {
                if (!map.containsKey(t.player)) {
                    map[t.player] = mutableListOf()
                }
                map[t.player]!!.add(t)
            }
            CollectMode.FinalOne -> {
                map[t.player] = mutableListOf(t)
            }
            CollectMode.FirstOne -> {
                if (!map.containsKey(t.player)) {
                    map[t.player] = mutableListOf(t)
                }
            }
        }
    }

    fun start() {
        proxy.start()
    }

    fun end() {
        proxy.end()
    }

    fun forEach(f: (Player, MutableList<T>) -> Unit) {
        map.forEach(f)
    }

    fun collect(): MutableMap<Player, MutableList<T>> {
        return map
    }

    fun iterator() = map.iterator()
}