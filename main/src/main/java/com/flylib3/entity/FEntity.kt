package com.flylib3.entity

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.util.FEntityType
import org.bukkit.Location
import org.bukkit.entity.Entity

open class FEntity<T : Entity>(override val flyLib: FlyLib, val entity: T) : FlyLibComponent {
    constructor(flyLib: FlyLib, location: Location, entityType: FEntityType<T>) : this(
        flyLib,
        entityType.spawn(location)
    )
}