package com.flylib.flylib3.util

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe
import kotlin.reflect.KClass

sealed class FEntityType<T : Entity>(val entityClass: KClass<T>) {
    object Player : FEntityType<org.bukkit.entity.Player>(org.bukkit.entity.Player::class)
    class entity<R : Entity>(entityClass: KClass<R>) : FEntityType<R>(entityClass)

    fun spawn(location: Location): T {
        return location.world.spawn(location, this.entityClass.java)
    }
}

/**
 * edit the tradings of merchant
 * @param lambda function to edit the tradings
 */
fun Merchant.editTrade(lambda: (MutableList<MerchantRecipe>) -> MutableList<MerchantRecipe>) {
    val recipes = recipes.toMutableList()
    val result = lambda(recipes)
    setRecipes(result)
}