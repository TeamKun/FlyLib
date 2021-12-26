package com.flylib3.resource.config

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent

class ConfigLoaderManager(override val flyLib: FlyLib) : FlyLibComponent {
    fun load(fileName: String): ConfigLoader {
        return ConfigLoader(flyLib, fileName)
    }

    operator fun get(fileName: String) = load(fileName)
}