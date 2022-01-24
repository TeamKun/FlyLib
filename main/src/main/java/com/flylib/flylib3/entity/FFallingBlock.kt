package com.flylib.flylib3.entity

import com.flylib.flylib3.FlyLib
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.FallingBlock

class FFallingBlock(flyLib: FlyLib, override val entity: FallingBlock) : FEntity<FallingBlock>(flyLib, entity) {
    constructor(flyLib: FlyLib, materialData: BlockData, location: Location) : this(
        flyLib,
        location.world.spawnFallingBlock(location, materialData)
    )

    constructor(flyLib: FlyLib, material: Material, location: Location) : this(
        flyLib,
        flyLib.plugin.server.createBlockData(material),
        location
    )
}