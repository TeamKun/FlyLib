package com.flylib.flylib3.test

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.FCommandEvent
import com.flylib.flylib3.event.ex.flylib.PlayerLeftClickEvent
import com.flylib.flylib3.toast.Toast
import com.flylib.flylib3.toast.ToastGenerator
import com.flylib.flylib3.util.command
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import kotlin.reflect.full.createType

class ToastTest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("show") {
                terminal {
                    execute(this@ToastTest::set)
                }

                part<String>(String::class.createType(), { _, _, _, _ -> listOf() }, { it }, lazyMatcher = { true }) {
                    terminal {
                        execute(this@ToastTest::setTitle)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onLeftClick(e: PlayerLeftClickEvent) {
        show(e.player)
    }

    val players = mutableMapOf<Player, String>()
    fun set(e: FCommandEvent, str: String): Boolean {
        if (e.commandSender is Player) {
            players[e.commandSender] = "Title"
        }
        return true
    }

    fun setTitle(e: FCommandEvent, str: String, title: String): Boolean {
        if (e.commandSender is Player) {
            players[e.commandSender] = title
        }
        return true
    }

    fun show(p: Player) {
        if (players.containsKey(p)) {
            showWithTitle(p, players[p]!!)
        }
    }

    fun showWithTitle(p: Player, title: String) {
        val toast = Toast(ToastGenerator(Material.DIAMOND, title, "Hi!From Toast!"), flyLib)
        toast.showTo(p)
    }

    override fun disable() {
    }
}