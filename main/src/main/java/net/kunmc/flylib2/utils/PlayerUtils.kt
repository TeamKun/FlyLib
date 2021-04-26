package net.kunmc.flylib2.utils

import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player

fun Player.displayStringName(): String {
    val n = displayName() as TextComponent
    return n.content()
}