package com.flylib.spawnegg

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class SpawnEggUtil {
    fun getEntityType(stack: ItemStack): EntityType? {
        val mobName = stack.type.name.replace("_SPAWN_EGG", "").toLowerCase()
        val entityType = EntityType.fromName(mobName)
        return entityType
    }

    fun getSpawnMaterial(type: EntityType): Material? {
        val itemName = type.name + "_SPAWN_EGG"
        val material = Material.getMaterial(itemName)
        return material
    }
}
