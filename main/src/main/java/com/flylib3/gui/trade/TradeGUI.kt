package com.flylib3.gui.trade

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.gui.FGUIComponent
import com.flylib3.gui.Pos
import com.flylib3.util.event
import com.flylib3.util.filter
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.TradeSelectEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe

class TradeGUI(override val flyLib: FlyLib, val entity: Merchant) :
    FGUIComponent<TradeGUIComponent?, Unit, Int, TradeGUIPos> {
    constructor(flyLib: FlyLib, name: Component) : this(flyLib, Bukkit.createMerchant(name))


    override fun width(): Int = 1
    override fun height(): Int = entity.recipeCount
    override fun open(p: Player, forceKeepOpen: Boolean) {
        open(p, forceKeepOpen, true)
    }

    /**
     * @param forceOpen even if player is trading,open this TradeGUI
     */
    fun open(p: Player, forceKeepOpen: Boolean = false, forceOpen: Boolean = true) {
        p.openMerchant(entity, forceOpen)
        if (forceKeepOpen) {
            event<InventoryCloseEvent, Boolean> {
                return@event it.player == p && checkIfInventoryIsThis(it.inventory)
            }.then {
                if (closer.contains(p)) {
                    closer.remove(p)
                } else {
                    open(p, true, forceOpen)
                }
            }
        }
        seer.add(p)
    }

    override fun close(p: Player) {
        closer.add(p)
        p.closeInventory()
        seer.remove(p)
    }

    /**
     * copy all data from real merchant trading data
     */
    fun syncFromMerchant() {
        val map = mutableMapOf<TradeGUIPos, TradeGUIComponent>()
        entity.recipes.forEachIndexed { index, merchantRecipe ->
            map[TradeGUIPos(index)] = TradeGUIComponent(
                merchantRecipe,
                TradeGUIPos(index), this,
                flyLib
            )
        }
    }

    fun syncToMerchant() {
        entity.recipes = listOf()
        val res = mutableListOf<MerchantRecipe>()
        components
            .map { Pair(it.key, it.value) }
            .toMutableList()
            .sortedBy { it.first.y }
            .forEach { (pos, component) ->
                res.add(component.recipe)
            }
        entity.recipes = res
    }

    /**
     * @return if the inventory is the inventory of this Merchant
     */
    fun checkIfInventoryIsThis(inventory: Inventory): Boolean {
        return inventory.type == InventoryType.MERCHANT && inventory.holder != null && inventory.holder is Merchant && inventory.holder == entity
    }


    private var components = mutableMapOf<TradeGUIPos, TradeGUIComponent>()

    override fun get(x: Unit, y: Int): TradeGUIComponent? {
        return this[TradeGUIPos(y)]
    }

    override fun get(pos: TradeGUIPos): TradeGUIComponent? {
        syncFromMerchant()
        return components[pos]
    }

    override fun getAll(): List<TradeGUIComponent> = components.values.toMutableList()

    override fun getAllPos(): List<TradeGUIPos> {
        return components.keys.toMutableList()
    }

    override fun set(x: Unit, y: Int, t: TradeGUIComponent?) {
        set(TradeGUIPos(y), t)
    }

    operator fun set(y: Int, recipe: MerchantRecipe) {
        set(TradeGUIPos(y), TradeGUIComponent(recipe, TradeGUIPos(y), this, flyLib))
    }

    override fun set(pos: TradeGUIPos, t: TradeGUIComponent?) {
        if (t != null) {
            components[pos] = t
        } else {
            components.remove(pos)
        }

        syncToMerchant()
    }

    private val closer = mutableListOf<Player>()
    private val seer = mutableListOf<Player>()
    override fun getAllSeer(): List<Player> = seer
}

class TradeGUIPos(val y: Int) : Pos<Unit, Int>() {
    override fun x() = Unit
    override fun y(): Int = this.y
    override fun clone(): Pos<Unit, Int> {
        return TradeGUIPos(y)
    }

    override fun equals(other: Any?): Boolean {
        return other is TradeGUIPos && other.y == y
    }

    override fun hashCode(): Int {
        return y
    }
}

class TradeGUIComponent(
    val recipe: MerchantRecipe, val pos: TradeGUIPos, val gui: TradeGUI,
    override val flyLib: FlyLib
) : FlyLibComponent {
    /**
     * Listener for when the player clicks on this trade
     */
    fun onTradeSelect(f: (TradeGUIComponent, TradeSelectEvent) -> Unit) {
        event<TradeSelectEvent, TradeSelectEvent?> {
            if (gui.checkIfInventoryIsThis(it.inventory)) {
                return@event it
            } else {
                return@event null
            }
        }.filter { tradeSelectEvent, _ ->
            tradeSelectEvent != null && tradeSelectEvent.index == pos.y
        }.then {
            f(this@TradeGUIComponent, it!!)
        }
    }

    /**
     * Listener for when the player trade on this trade
     */
    fun onTrade(f: (TradeGUIComponent, VillagerAcquireTradeEvent) -> Unit) {
        event<VillagerAcquireTradeEvent, VillagerAcquireTradeEvent?> {
            if (gui.entity == it.entity && recipe == it.recipe) {
                return@event it
            } else {
                return@event null
            }
        }.filter { villagerAcquireTradeEvent, _ ->
            villagerAcquireTradeEvent != null
        }.then {
            f(this@TradeGUIComponent, it!!)
        }
    }
}