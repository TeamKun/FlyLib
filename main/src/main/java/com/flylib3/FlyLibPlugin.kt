package com.flylib3

import org.bukkit.plugin.java.JavaPlugin

open class FlyLibPlugin : JavaPlugin() {
    @Suppress("LeakingThis")
    val flylib: FlyLib = flylib(this)

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}