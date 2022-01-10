package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.gui.trade.TradeGUI
import com.flylib3.gui.trade.TradeGUIComponent
import com.flylib3.item.ItemData
import com.flylib3.util.command
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe

class TradeGUITest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("open") {
                terminal {
                    execute(this@TradeGUITest::open)
                }
            }
        }
    }

    fun open(e: FCommandEvent, str: String): Boolean {
        val gui = TradeGUI(flyLib, Component.text("Test Villager"))
        gui[0] = MerchantRecipe(
            ItemData(material = Material.DIAMOND, name = Component.text("なんかすぺしゃるなダイヤ")).build(),
            10000
        ).also {
            it.addIngredient(
                ItemData(material = Material.DIAMOND).build()
            )
        }
        return if (e.commandSender is Player) {
            gui.open(e.commandSender)
            true
        } else {
            e.commandSender.sendMessage("Only player can open trade gui")
            false
        }
    }

    override fun disable() {
    }
}