package com.flylib3.event.ex.flylib

import com.flylib3.event.ex.ExternalEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Player Right Click Event
 * @note Bukkit seems not to continue to fire PlayerInteractEvent when right-air clicking
 */
class PlayerRightClickEvent(val player: Player, val event: PlayerInteractEvent) : ExternalEvent() {
}