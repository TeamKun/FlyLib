package com.flylib3

import org.bukkit.plugin.java.JavaPlugin

open class FlyLibPlugin : JavaPlugin() {
    @Suppress("LeakingThis")
    private val flylib: FlyLib = flylib(this)

    override fun onEnable() {
        flylib.lifeCycle.onEnable()
    }

    open fun onWait() {
        flylib.lifeCycle.onWait()
    }

    open fun onPrepare() {
        flylib.lifeCycle.onPrepare()
    }

    open fun onAction() {
        flylib.lifeCycle.onAction()
    }

    override fun onDisable() {
        flylib.lifeCycle.onDisable()
    }
}