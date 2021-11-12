package com.flylib3.resource

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.resource.config.ConfigManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.InputStream

class ResourceManager(flyLib: FlyLib) : FlyLibComponent(flyLib) {
    val config = ConfigManager(flyLib, this)

    operator fun get(path: String): InputStream? {
        return flyLib.plugin.getResource(path)
    }

    fun <T> get(path: String, f: (InputStream) -> T): T? {
        val i = this[path]
        if (i != null) {
            f(i)
        }
        return null
    }

    fun getYaml(path: String): YamlConfiguration? {
        if (!(path.endsWith(".yml") || path.endsWith(".yaml"))) return null
        return get(path) {
            return@get YamlConfiguration.loadConfiguration(it.reader())
        }
    }

    init {
        config.init()
    }
}