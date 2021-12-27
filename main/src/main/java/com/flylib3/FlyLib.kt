package com.flylib3

import com.flylib3.command.CommandManager
import com.flylib3.event.EventManager
import com.flylib3.resource.ResourceManager
import com.flylib3.task.FTaskManager
import org.bukkit.plugin.java.JavaPlugin

class FlyLib internal constructor(val plugin: JavaPlugin) {
    val resource = ResourceManager(this)
    val log = FlyLibLogger(this)
    val command = CommandManager(this)
    val event = EventManager(this)
    val task = FTaskManager(this)
}

fun flylib(plugin: JavaPlugin): FlyLib {
    return FlyLib(plugin)
}