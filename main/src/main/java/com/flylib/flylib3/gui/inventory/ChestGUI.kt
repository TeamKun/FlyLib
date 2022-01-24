package com.flylib.flylib3.gui.inventory

import com.flylib.flylib3.FlyLib
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.InventoryType

class ChestGUI(override val flyLib: FlyLib, name: Component, val height: Int) :
    InventoryGUI(InventoryType.CHEST, name, flyLib, height) {
    companion object {
        const val width = 9
    }

    private val entries = Array(width * height) {
        InventoryGUIEntry(InventoryPos((it % width) + 1, (it / width) + 1), this)
    }

    override fun getInternal(pos: InventoryPos): InventoryGUIEntry {
        return entries[pos.index()]
    }

    override fun setInternal(pos: InventoryPos, entry: InventoryGUIEntry) {
        entries[pos.index()] = entry
        sync(pos)
    }

    override fun getAllPos(): List<InventoryPos> = entries.map { it.pos }
    override fun width(): Int = width
    override fun height(): Int = height
    override fun getAll(): List<InventoryGUIEntry> {
        return getAllPos().map { getInternal(it) }
    }
}