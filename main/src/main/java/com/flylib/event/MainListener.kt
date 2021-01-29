package com.flylib.event

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction1

class MainListener : Listener {
    companion object {
        var instance: MainListener = MainListener()
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        Events.InventoryClickEvent.execute(e)
    }

    @EventHandler
    fun onInventoryDrop(e: InventoryMoveItemEvent) {
        Events.InventoryMoveEvent.execute(e)
    }

    @EventHandler
    fun onInventoryDrop(e: InventoryCloseEvent) {
        Events.InventoryCloseEvent.execute(e)
    }
}

enum class Events(val generic_event_name: String) {
    InventoryClickEvent("InventoryClickEvent"),
    InventoryCloseEvent("InventoryCloseEvent"),
    InventoryMoveEvent("InventoryMoveItemEvent");
    private val register: ArrayList<KFunction1<Event, Unit>> = arrayListOf()
    fun register(f: KFunction1<Event, Unit>) {
        register.add(f)
    }

    fun execute(e: Event) {
        for (r in register) {
            r(e)
        }
    }
}

