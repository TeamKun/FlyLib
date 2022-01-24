package com.flylib.flylib3.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable


data class ItemData(
    val material: Material,
    val amount: Int = 1,
    val name: Component? = null,
    val lore: List<Component> = listOf(),
    val damage: Int = 0,
    val enchantments: List<Pair<Enchantment, Int>> = listOf(),
    val ignoreEnchantLimit: Boolean = true
) {
    fun build() = buildItem(this)
}

fun buildItem(data: ItemData): ItemStack {
    val stack = ItemStack(data.material, data.amount)
    stack.editMeta {
        if (data.name != null) {
            it.displayName(data.name)
        }

        if (data.lore.isNotEmpty()) {
            it.lore(data.lore)
        }

        if (data.damage > 0 && it is Damageable) {
            it.damage = data.damage
        }

        if (data.enchantments.isNotEmpty()) {
            data.enchantments.forEach { e ->
                it.addEnchant(e.first, e.second, data.ignoreEnchantLimit)
            }
        }
    }

    return stack
}