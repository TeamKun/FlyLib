package com.flylib.flylib3.test

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.FCommandEvent
import com.flylib.flylib3.util.*
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent

class TaskTest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("run") {
                terminal {
                    execute(this@TaskTest::call)
                }
            }
        }

        task {
            println("AAA")
        }.then {
            println("BBB")
        }.run()

        task {
            println("CCC")
        }.filter { _, _ ->
            return@filter false
        }.then {
            println("This will not be called.")
        }.run()

        event<InventoryOpenEvent, Player> {
            return@event it.player as Player
        }.then {
            it.sendMessage("You opened an inventory.")
        }
    }

    override fun disable() {
    }

    fun call(e: FCommandEvent, str: String): Boolean {
        task {
            val players = flyLib.plugin.server.onlinePlayers

            players.forEach {
                it.sendMessage("Now,Shuffling")
            }

            // Process Something
            return@task players.shuffled()
        }
            .wait(20 * 2)
            .then {
                it.forEachIndexed { index, player ->
                    player.sendMessage("Sending With Delay!")
                    player.sendMessage("Your index is $index")
                }
            }.run()
        return true
    }
}