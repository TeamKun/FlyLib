package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.SimpleFListener
import org.bukkit.event.inventory.InventoryOpenEvent

class EventStreamTest : FlyLibPlugin() {
    override fun enable() {
        val listener = SimpleFListener(InventoryOpenEvent::class) {
            println("InventoryOpenEvent")
        }
        flylib.log.info { "${it}Register listener:${flylib.event.register(listener)}" }
    }

    override fun disable() {
    }
}