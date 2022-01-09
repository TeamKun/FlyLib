package com.flylib3.gui.inventory

import com.flylib3.FlyLib
import com.flylib3.gui.FGUIComponent
import com.flylib3.gui.Pos
import com.flylib3.util.ready
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

/**
 * Represents an inventory GUI.
 * @param name The name of the inventory.
 * @param inventoryType The type of inventory.
 */
abstract class InventoryGUI(inventoryType: InventoryType, name: Component, val fl: FlyLib) :
    FGUIComponent<InventoryGUIEntry, Int, Int, InventoryPos> {
    val inventory = Bukkit.createInventory(null, inventoryType, name)
    override val flyLib: FlyLib = fl

    init {
        registerEvent()
    }

    /**
     * Registers the inventory GUI's events.
     */
    fun registerEvent() {
        fl.event.stream(InventoryClickEvent::class).filter {
            it.inventory == this.inventory
        }.forEach {
            onClick(it.rawSlot, it)
        }
    }

    /**
     * Passes the click event to the entry.
     */
    fun onClick(index: Int, e: InventoryClickEvent) {
        println("onClick")
        val pos = getAllPos().firstOrNull { it.index() == index }
        if (pos != null) {
            val entry = get(pos)
            entry.onClick(e)
        }
    }

    /**
     * sync all entry to this gui
     */
    fun sync() {
        getAllPos().forEach {
            sync(it)
        }

        getAllSeer().forEach {
            it.updateInventory()
        }
    }

    fun sync(pos: InventoryPos) {
        val entry = get(pos)
        val e = inventory.getItem(pos.index())
        if (e != null) {
            if (e.isSimilar(entry.itemStack)) {
                return
            }
        } else {
            if (entry.itemStack == null) {
                return
            }
        }
        inventory.setItem(pos.index(), entry.itemStack)
    }

    private val seer = mutableListOf<Player>()
    private val closer = mutableListOf<Player>()

    override fun open(p: Player, forceKeepOpen: Boolean) {
        p.openInventory(inventory)
        seer.add(p)
        if (forceKeepOpen) {
            flyLib.event.stream(InventoryCloseEvent::class).filter {
                it.inventory == inventory && it.player == p
            }.forEach {
                if (closer.contains(p)) {
                    closer.remove(p)
                } else {
                    open(p, true)
                }
            }
        }
    }

    override fun close(p: Player) {
        closer.add(p)
        p.closeInventory()
        seer.remove(p)
    }

    final override fun getAllSeer(): List<Player> {
        val list = inventory.viewers.toList().mapNotNull { it as? Player }
        if (list != seer) {
            throw IllegalStateException("seer list is not sync")
        }
        return list
    }

    fun checkIfPosInGUI(pos: InventoryPos): Boolean {
        return getAllPos().contains(pos)
    }

    fun checkPos(pos: InventoryPos) {
        if (!checkIfPosInGUI(pos)) {
            throw IllegalArgumentException("pos $pos is not in this gui")
        }
    }

    final override fun get(pos: InventoryPos): InventoryGUIEntry {
        checkPos(pos)
        return getInternal(pos)
    }

    abstract fun getInternal(pos: InventoryPos): InventoryGUIEntry

    final override fun set(pos: InventoryPos, entry: InventoryGUIEntry) {
        checkPos(pos)
        setInternal(pos, entry)
    }

    abstract fun setInternal(pos: InventoryPos, entry: InventoryGUIEntry)

    final override fun get(x: Int, y: Int): InventoryGUIEntry {
        return this[InventoryPos(x, y)]
    }

    final override fun set(x: Int, y: Int, t: InventoryGUIEntry) {
        set(InventoryPos(x, y), t)
    }
}

open class InventoryGUIEntry(val pos: InventoryPos, val gui: InventoryGUI) {
    var itemStack: ItemStack? = null
        set(value) {
            field = value
            sync()
        }

    /**
     * sync this entry to gui
     */
    fun sync() {
        gui.sync(pos)
    }

    /**
     * Called when the player click this entry.
     */
    var click: (InventoryClickEvent) -> Unit = {}
    final fun onClick(e: InventoryClickEvent) {
        if (isCancelClickEvent) {
            e.isCancelled = true
        }
        click(e)
    }

    /**
     * if this entry is clickable,not movable
     */
    var isCancelClickEvent: Boolean = true
}

// The position of the inventory
// X is the row, Y is the column
// The top left is (1, 1)
class InventoryPos(val X: Int, val Y: Int) : Pos<Int, Int>() {
    override fun x(): Int = X
    override fun y(): Int = Y
    fun index(): Int = (X - 1) + (Y - 1) * 9
    override fun clone(): InventoryPos = InventoryPos(X, Y)
}