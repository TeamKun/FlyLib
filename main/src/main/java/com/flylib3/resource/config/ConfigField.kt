package com.flylib3.resource.config

import com.flylib3.Disposable

class ConfigField(path: String, private val configLoader: ConfigLoader) : ConfigUpdateListener, Disposable<Unit> {
    init {
        configLoader.listeners.add(this)
    }

    var path = path
        set(value) {
            field = value
            reload()
        }

    var data: Any? = null
        private set

    override fun onUpdate(config: ConfigLoader) {
        reload()
    }

    private fun reload() {
        this.data = configLoader.config.get(path)
    }

    private fun update(o: Any?) {
        configLoader.config.set(path, o)
    }

    fun set(s: Any?) {
        this.data = s
        update(s)
    }

    override fun dispose(t: Unit) {
        configLoader.listeners.remove(this)
    }
}