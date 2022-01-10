package com.flylib3.entity

import com.destroystokyo.paper.entity.Pathfinder
import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import org.bukkit.Location
import org.bukkit.entity.Mob

class FPathFinder<T : Mob>(val fMob: FMob<T>, override val flyLib: FlyLib = fMob.flyLib) : FlyLibComponent {
    val bukkitPathFinder = fMob.entity.pathfinder

    /**
     * Move this mob to the location,
     * @return If the pathfinding was successfully started
     */
    fun moveTo(loc: Location, speed: Double = 1.0): Boolean {
        return bukkitPathFinder.moveTo(loc, speed)
    }

    /**
     * @return if this mob is currently moving to a location
     */
    fun isMoving() = bukkitPathFinder.hasPath()

    /**
     * Stop Path Finding and to navigate to the location
     */
    fun stop() {
        bukkitPathFinder.stopPathfinding()
    }

    /**
     * @return Nullable PathResult to the location
     */
    fun find(to: Location): Pathfinder.PathResult? {
        return bukkitPathFinder.findPath(to)
    }

    /**
     * @return whether the mob is reachable to the target location
     */
    fun isReachable(to: Location): Boolean {
        return find(to) != null
    }
}