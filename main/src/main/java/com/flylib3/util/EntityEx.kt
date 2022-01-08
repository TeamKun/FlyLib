package com.flylib3.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import kotlin.reflect.KClass

sealed class FEntityType<T : Entity>(val entityClass: KClass<T>) {
    object Player : FEntityType<org.bukkit.entity.Player>(org.bukkit.entity.Player::class)
    class entity<R : Entity>(entityClass: KClass<R>) : FEntityType<R>(entityClass)

    fun spawn(location: Location): T {
        return location.world.spawn(location, this.entityClass.java)
    }
}
