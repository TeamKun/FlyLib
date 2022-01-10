package com.flylib3.event.ex.flylib

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.util.event
import com.flylib3.util.log
import com.flylib3.util.ready
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * This File adds Useful Event for FlyLib/Bukkit
 */
class FlyLibDefaultExEvents(override val flyLib: FlyLib) : FlyLibComponent {
    init {
        ready {
            onReady()
        }
    }

    private fun onReady() {
        log("[FlyLibDefaultExEvents]Registering Default Events.")
        registerClick()
    }

    private fun registerClick() {
        event<PlayerInteractEvent, Unit> {
            when (it.action) {
                Action.LEFT_CLICK_BLOCK,
                Action.LEFT_CLICK_AIR -> {
                    flyLib.event.callEvent(PlayerLeftClickEvent(it.player, it))
                }
                Action.RIGHT_CLICK_BLOCK,
                Action.RIGHT_CLICK_AIR -> {
                    flyLib.event.callEvent(PlayerRightClickEvent(it.player, it))
                }
                Action.PHYSICAL -> {
                    // This is not clicking.
                }
            }
        }
    }
}