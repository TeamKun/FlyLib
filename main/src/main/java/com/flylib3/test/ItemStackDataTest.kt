package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.item.ItemData
import com.flylib3.util.command
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class ItemStackDataTest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("give") {
                terminal {
                    execute(this@ItemStackDataTest::giveItem)
                }
            }

            part("write") {
                terminal {
                    execute(this@ItemStackDataTest::writeItem)
                }
            }


            part("read") {
                terminal {
                    execute(this@ItemStackDataTest::readItem)
                }
            }
        }
    }

    override fun disable() {
    }

    val itemData = ItemData(
        material = Material.DIAMOND,
        name = Component.text("すっご～いダイヤモンド"),
        amount = 1,
        lore = listOf(Component.text("説明"), Component.newline(), Component.text("これはダイヤモンドです")),
        damage = 2
    )

    fun giveItem(e: FCommandEvent, str: String): Boolean {
        val stack = itemData.build()
        if (e.commandSender is Player) {
            e.commandSender.inventory.addItem(stack)
        }
        return true
    }

    fun writeItem(e: FCommandEvent, str: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            val result = stack.setData("Test")
            e.commandSender.sendMessage("setData: $result")
            return true
        } else {
            e.commandSender.sendMessage("Player only")
            return false
        }
    }

    fun readItem(e: FCommandEvent, str: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            if (stack.type == Material.DIAMOND) {
                val data = stack.getData<String>()
                e.commandSender.sendMessage("Data: $data")
            }
            return true
        } else {
            e.commandSender.sendMessage("Only player can use this command")
            return false
        }
    }
}