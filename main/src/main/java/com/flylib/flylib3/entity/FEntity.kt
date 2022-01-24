package com.flylib.flylib3.entity

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent
import com.flylib.flylib3.util.FEntityType
import org.bukkit.Location
import org.bukkit.entity.Entity

open class FEntity<T : Entity>(override val flyLib: FlyLib, open val entity: T) : FlyLibComponent {
    constructor(flyLib: FlyLib, location: Location, entityType: FEntityType<T>) : this(
        flyLib,
        entityType.spawn(location)
    )

    var speed: Double
        get() = entity.velocity.length()
        set(value) {
            val velocity = entity.velocity
            val direction = velocity.normalize()
            entity.velocity = direction.multiply(value)
        }
}