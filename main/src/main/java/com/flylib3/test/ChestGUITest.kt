package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.gui.inventory.ChestGUI
import com.flylib3.util.command
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ChestGUITest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("openGUI") {
                terminal {
                    execute(this@ChestGUITest::open)
                }
            }
        }
    }

    override fun disable() {
    }

    fun open(event: FCommandEvent, str: String): Boolean {
        return if (event.commandSender is Player) {
            val gui = ChestGUI(flylib, Component.text("Test ChestGUI"), 4)
            gui.open(event.commandSender)
            gui[1, 1].itemStack = ItemStack(Material.DIAMOND).also { it.amount = 64 }
            gui[1, 1].click = {
                it.whoClicked.sendMessage("Clicked!")
            }
            true
        } else {
            event.commandSender.sendMessage("This command can only be executed by player")
            false
        }
    }
}