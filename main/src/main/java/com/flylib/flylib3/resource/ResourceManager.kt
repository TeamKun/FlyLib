package com.flylib.flylib3.resource

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent
import com.flylib.flylib3.resource.config.ConfigLoaderManager
import org.bukkit.configuration.file.YamlConfiguration
import java.io.InputStream

class ResourceManager(override val flyLib: FlyLib) : FlyLibComponent {
    val config = ConfigLoaderManager(flyLib)

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
}