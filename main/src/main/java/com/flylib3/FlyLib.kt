package com.flylib3

import com.flylib3.command.CommandManager
import com.flylib3.event.EventManager
import com.flylib3.log.FlyLibLogger
import com.flylib3.resource.ResourceManager
import com.flylib3.task.FTaskManager
import org.bukkit.plugin.java.JavaPlugin

class FlyLib internal constructor(val plugin: JavaPlugin) {
    // ready Listener
    private val ready = mutableListOf<() -> Unit>()
    var isReady = false
        private set

    fun onReady(lambda: () -> Unit) {
        if (isReady) lambda()
        else ready.add(lambda)
    }

    val resource = ResourceManager(this)
    val log = FlyLibLogger(this)
    val command = CommandManager(this)
    val event = EventManager(this)
    val task = FTaskManager(this)
    val persistentDataManager = PersistentDataManager(this)

    init {
        isReady = true
        ready.forEach { it() }
    }
}

fun flylib(plugin: JavaPlugin): FlyLib {
    return FlyLib(plugin)
}