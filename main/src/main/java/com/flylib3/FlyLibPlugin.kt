package com.flylib3

import com.flylib3.event.ex.ExternalEvent
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

abstract class FlyLibPlugin : JavaPlugin(), Listener {
    @Suppress("LeakingThis")
    val flylib: FlyLib = flylib(this)

    final override fun onEnable() {
        onEnableFlyLib()
        enable()
    }

    private fun onEnableFlyLib() {
        server.pluginManager.registerEvents(this, this)
    }

    abstract fun enable()

    final override fun onDisable() {
        disable()
        onDisableFlyLib()
    }

    private fun onDisableFlyLib() {
    }

    abstract fun disable()

    /**
     * Call External Event in this plugin.
     * @see com.flylib3.event.EventManager
     */
    fun callEvent(event: ExternalEvent) {
        flylib.event.callEvent(event)
    }
}