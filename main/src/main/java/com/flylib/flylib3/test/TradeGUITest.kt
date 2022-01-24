package com.flylib.flylib3.test

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.FCommandEvent
import com.flylib.flylib3.gui.trade.TradeGUI
import com.flylib.flylib3.gui.trade.Trading
import com.flylib.flylib3.item.ItemData
import com.flylib.flylib3.util.command
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

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
        gui[0] = Trading(
            firstIngredient = ItemData(material = Material.DIAMOND).build(),
            result = ItemData(
                material = Material.DIAMOND,
                name = Component.text("なんかすぺしゃるなダイヤ")
            ).build(),
            maxUses = 10
        )

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