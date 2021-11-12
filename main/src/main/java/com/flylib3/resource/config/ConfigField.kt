package com.flylib3.resource.config

import com.flylib3.Disposable

class ConfigField(path: String, private val configManager: ConfigManager) : ConfigUpdateListener, Disposable<Unit> {
    init {
        configManager.listeners.add(this)
    }

    var path = path
        set(value) {
            field = value
            reload()
        }

    var data: Any? = null
        private set

    override fun onUpdate(config: ConfigManager) {
        reload()
    }

    private fun reload() {
        this.data = configManager.config.get(path)
    }

    private fun update(o: Any?) {
        configManager.config.set(path, o)
    }

    fun set(s: Any?) {
        this.data = s
        update(s)
    }

    override fun dispose(t: Unit) {
        configManager.listeners.remove(this)
    }
}