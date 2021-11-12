package com.flylib3.resource.config

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.resource.ResourceManager
import org.bukkit.configuration.file.YamlConfiguration

class ConfigManager(flyLib: FlyLib, private val resource: ResourceManager) : FlyLibComponent(flyLib) {
    var configFileName = "config.yml"
        set(value) {
            field = value
            reloadConfig()
        }

    lateinit var config: YamlConfiguration
        private set

    fun reloadConfig() {
        val c = resource.getYaml(configFileName)
        if (c != null) {
            config = c
            listeners.forEach { it.onUpdate(this) } // Listener is called here when the reloading is success
        } else {
            // config is loaded but once reload,newConfig is not found
            flyLib.log.warn { "${it}ConfigFile:${configFileName} is not longer exist." }
        }
    }

    operator fun get(path: String): ConfigField {
        return ConfigField(path, this)
    }

    internal val listeners = mutableListOf<ConfigUpdateListener>()

    internal fun init() {
        reloadConfig()
    }
}