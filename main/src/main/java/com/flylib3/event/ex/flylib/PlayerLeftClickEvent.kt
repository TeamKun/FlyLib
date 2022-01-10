package com.flylib3.event.ex.flylib

import com.flylib3.event.ex.ExternalEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Player left click event
 */
class PlayerLeftClickEvent(val player: Player,val event: PlayerInteractEvent) : ExternalEvent() {
}