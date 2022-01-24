package com.flylib.flylib3.resource.config

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent
import org.bukkit.configuration.file.YamlConfiguration

class ConfigLoader(override val flyLib: FlyLib, val fileName: String) : FlyLibComponent {
    private val resource = flyLib.resource

    lateinit var config: YamlConfiguration
        private set

    fun reloadConfig() {
        val c = resource.getYaml(fileName)
        if (c != null) {
            config = c
            listeners.forEach { it.onUpdate(this) } // Listener is called here when the reloading is success
        } else {
            // config is loaded but once reload,newConfig is not found
            flyLib.log.warn { "${it}ConfigFile:${fileName} is not longer exist." }
        }
    }

    val listeners = mutableListOf<ConfigUpdateListener>()

    operator fun get(path: String): ConfigField {
        return ConfigField(path, this)
    }

    init {
        reloadConfig()
    }
}