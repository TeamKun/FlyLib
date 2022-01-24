package com.flylib.flylib3.test

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.FCommandEvent
import com.flylib.flylib3.gui.inventory.ChestGUI
import com.flylib.flylib3.util.command
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
            val gui = ChestGUI(flyLib, Component.text("Test ChestGUI"), 6)
            gui.open(event.commandSender as Player)

            gui[1,1].apply {
                itemStack = ItemStack(Material.DIAMOND).also { it.amount = 64 }
                click = {
                    it.whoClicked.sendMessage("Clicked!")
                }
            }
            true
        } else {
            event.commandSender.sendMessage("This command can only be executed by player")
            false
        }
    }
}