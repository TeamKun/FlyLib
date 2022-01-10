package com.flylib3.gui.trade

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.MerchantRecipe

/**
 * Class for trading.
 * require ingredient at least one. amd result.
 *
 * for parameters,please refer to [MerchantRecipe]
 */
class Trading(
    val firstIngredient: () -> ItemStack,
    val secondIngredient: (() -> ItemStack)? = null,
    val result: () -> ItemStack,
    val uses: Int = 0,
    val maxUses: Int,
    val experienceReward: Boolean = false,
    val villagerExperience: Int = 0,
    val priceMultiplier: Float = 0.0f,
    val ignoreDiscounts: Boolean = false
) {
    constructor(
        firstIngredient: ItemStack,
        secondIngredient: ItemStack? = null,
        result: ItemStack,
        uses: Int = 0,
        maxUses: Int,
        experienceReward: Boolean = false,
        villagerExperience: Int = 0,
        priceMultiplier: Float = 0.0f,
        ignoreDiscounts: Boolean = false
    ) : this(
        { firstIngredient.clone() },
        if (secondIngredient == null) {
            null
        } else {
            { secondIngredient }
        },
        { result },
        uses,
        maxUses,
        experienceReward,
        villagerExperience,
        priceMultiplier,
        ignoreDiscounts
    )


    fun build(): MerchantRecipe {
        return MerchantRecipe(
            result(),
            uses,
            maxUses,
            experienceReward,
            villagerExperience,
            priceMultiplier,
            ignoreDiscounts
        ).also {
            it.addIngredient(firstIngredient())
            if (secondIngredient != null) {
                it.addIngredient(secondIngredient!!())
            }
        }
    }
}