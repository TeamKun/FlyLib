package com.flylib3.util

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer

fun <T> ItemStack.dataContainer(lambda: (PersistentDataContainer) -> T): T? {
    var t: T? = null
    if (!editMeta {
            t = lambda(it.persistentDataContainer)
        }){
        return null
    }
    return t
}