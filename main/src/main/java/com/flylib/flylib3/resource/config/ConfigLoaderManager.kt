package com.flylib.flylib3.resource.config

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent

class ConfigLoaderManager(override val flyLib: FlyLib) : FlyLibComponent {
    fun load(fileName: String): ConfigLoader {
        return ConfigLoader(flyLib, fileName)
    }

    operator fun get(fileName: String) = load(fileName)
}