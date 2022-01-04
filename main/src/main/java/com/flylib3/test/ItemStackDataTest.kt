package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.item.ItemData
import com.flylib3.item.ObjectPersistentDataType
import com.flylib3.util.command
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.time.LocalDate

class ItemStackDataTest : FlyLibPlugin() {
    override fun enable() {
        // Register Test PersistentDataType
        flyLib.item.registerPersistentDataType(LocalDatePersistentDataType())

        command("testCommand") {
            part("give") {
                terminal {
                    execute(this@ItemStackDataTest::giveItem)
                }
            }

            part("date") {
                part("write") {
                    terminal {
                        execute(this@ItemStackDataTest::writeItemDate)
                    }
                }

                part("read") {
                    terminal {
                        execute(this@ItemStackDataTest::readItemDate)
                    }
                }
            }

            part("list") {
                part("write") {
                    terminal {
                        execute(this@ItemStackDataTest::writeItemList)
                    }
                }

                part("read") {
                    terminal {
                        execute(this@ItemStackDataTest::readItemList)
                    }
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

    fun writeItemDate(e: FCommandEvent, str: String, str1: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            val result = stack.setData(LocalDate.now())
            e.commandSender.sendMessage("setData: $result")
            return true
        } else {
            e.commandSender.sendMessage("Player only")
            return false
        }
    }

    fun readItemDate(e: FCommandEvent, str: String, str1: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            if (stack.type == Material.DIAMOND) {
                val data = stack.getData<LocalDate>()
                e.commandSender.sendMessage("Data: $data")
            }
            return true
        } else {
            e.commandSender.sendMessage("Only player can use this command")
            return false
        }
    }

    fun writeItemList(e: FCommandEvent, str: String, str1: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            val result = stack.setList(listOf(1, 2, 3))
            e.commandSender.sendMessage("setData: $result")
            return true
        } else {
            e.commandSender.sendMessage("Player only")
            return false
        }
    }

    fun readItemList(e: FCommandEvent, str: String, str1: String): Boolean {
        if (e.commandSender is Player) {
            val stack = e.commandSender.inventory.itemInMainHand
            if (stack.type == Material.DIAMOND) {
                val data = stack.getList<Int>()
                e.commandSender.sendMessage("Data: $data")
            }
            return true
        } else {
            e.commandSender.sendMessage("Only player can use this command")
            return false
        }
    }
}

class LocalDatePersistentDataType() :
    ObjectPersistentDataType<String, LocalDate>(String::class.java, LocalDate::class.java) {
    override fun toPrimitive(complex: LocalDate, context: PersistentDataAdapterContext): String {
        return complex.toString()
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): LocalDate {
        return LocalDate.parse(primitive)
    }
}