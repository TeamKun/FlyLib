package com.flylib.flylib3.resource.config

/**
 * This Listener will be called when the configManager reload the configFile
 */
interface ConfigUpdateListener {
    fun onUpdate(config: ConfigLoader)
}