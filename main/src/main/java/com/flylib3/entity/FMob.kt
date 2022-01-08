package com.flylib3.entity

import com.flylib3.FlyLib
import com.flylib3.util.FEntityType
import org.bukkit.Location
import org.bukkit.entity.Mob

class FMob<T : Mob>(override val flyLib: FlyLib, val mob: T) : FEntity<T>(flyLib, mob) {
    constructor(flyLib: FlyLib, location: Location, entityType: FEntityType<T>) : this(
        flyLib,
        entityType.spawn(location)
    )

    val fPathFinder = FPathFinder(this)

    /**
     * Completely freeze the mob.
     */
    fun freeze(isFreeze: Boolean, ignoreGravity: Boolean = false) {
        mob.setAI(isFreeze)
        mob.setGravity(!ignoreGravity)
    }

    /**
     * Navigate this mob to a location using bukkit pathfinder.
     */
    fun moveTo(loc: Location, speed: Double = 1.0): Boolean {
        return fPathFinder.moveTo(loc, speed)
    }
}