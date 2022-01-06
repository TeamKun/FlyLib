package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.SimpleFListener
import com.flylib3.util.info
import org.bukkit.event.inventory.InventoryOpenEvent

class EventStreamTest : FlyLibPlugin() {
    override fun enable() {
        val listener = SimpleFListener(InventoryOpenEvent::class) {
            println("InventoryOpenEvent")
        }
        info { "${it}Register listener:${flyLib.event.register(listener)}" }
    }

    override fun disable() {
    }
}