package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.entity.FFallingBlock
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.util.command
import com.flylib3.util.info
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.full.createType

class FallingSandTest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("spawn") {
                terminal {
                    execute(this@FallingSandTest::spawn)
                }

                part<Double>(
                    Double::class.createType(),
                    { _, _, _, _ -> listOf() },
                    { it.toDoubleOrNull() },
                    { true }
                ) {
                    terminal {
                        execute(this@FallingSandTest::spawnWithSpeed)
                    }
                }
            }
        }
    }

    fun spawn(e: FCommandEvent, str: String): Boolean {
        info { "spawn" }
        return if (e.commandSender is Player) {
            val falling = FFallingBlock(flyLib, Material.DIAMOND_BLOCK, e.commandSender.location)
            falling.entity.velocity = e.commandSender.location.direction
            falling.speed = 10.0
            true
        } else {
            e.commandSender.sendMessage("This command can only be executed by player")
            true
        }
    }

    fun spawnWithSpeed(e: FCommandEvent, str: String, speed: Double): Boolean {
        info { "${it}CalledSpeed" }
        return if (e.commandSender is Player) {
            val falling = FFallingBlock(flyLib, Material.DIAMOND_BLOCK, e.commandSender.location)
            falling.entity.velocity = e.commandSender.location.direction
            falling.speed = speed
            true
        } else {
            e.commandSender.sendMessage("This command can only be executed by player")
            true
        }
    }

    override fun disable() {
    }
}