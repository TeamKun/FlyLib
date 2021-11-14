package com.flylib3

import com.flylib3.command.FlyLibCommandManager
import com.flylib3.lifecycle.LifeCycleManager
import com.flylib3.resource.ResourceManager
import org.bukkit.plugin.java.JavaPlugin

class FlyLib internal constructor(val plugin: JavaPlugin) {
    val lifeCycle = LifeCycleManager(this)
    val resource = ResourceManager(this)
    val log = FlyLibLogger(this)
    val command = FlyLibCommandManager(this)
}

fun flylib(plugin: JavaPlugin): FlyLib {
    return FlyLib(plugin)
}