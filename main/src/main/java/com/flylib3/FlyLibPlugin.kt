package com.flylib3

import com.flylib3.event.ex.ExternalEvent
import com.flylib3.util.dataContainer
import com.flylib3.util.info
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

abstract class FlyLibPlugin : JavaPlugin(), Listener, FlyLibComponent {
    @Suppress("LeakingThis")
    override val flyLib: FlyLib = flylib(this)

    final override fun onEnable() {
        onEnableFlyLib()
        enable()
    }

    private fun onEnableFlyLib() {
        server.pluginManager.registerEvents(this, this)
    }

    abstract fun enable()

    final override fun onDisable() {
        disable()
        onDisableFlyLib()
    }

    private fun onDisableFlyLib() {
    }

    abstract fun disable()

    /**
     * Call External Event in this plugin.
     * @see com.flylib3.event.EventManager
     */
    fun callEvent(event: ExternalEvent) {
        flyLib.event.callEvent(event)
    }

    /**
     * @return the data container of the item.
     */
    inline fun <reified V : Any> ItemStack.getData(): V? {
        return flyLib.item.get<V>(this)
    }

    /**
     * set the data into the item.
     */
    inline fun <reified V : Any> ItemStack.setData(value: V): Boolean {
        return flyLib.item.set(this, value)
    }

    inline fun <reified T : Any> ItemStack.getList(): List<T>? {
        return flyLib.item.getList<T>(this)
    }

    inline fun <reified T : Any> ItemStack.setList(value: List<T>): Boolean {
        return flyLib.item.setList(this, value)
    }
}