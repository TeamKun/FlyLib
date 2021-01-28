package com.flylib.spawner

import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType

class EasySpawner(var spawner: CreatureSpawner) {
    companion object {
        fun get(b: Block): EasySpawner? {
            if (b.state is CreatureSpawner) {
                return EasySpawner(b.state as CreatureSpawner)
            }
            return null
        }
    }

    fun setMonster(entity: EntityType) { spawner.spawnedType = entity }
    fun getMonster() = spawner.spawnedType

    fun setDelay(ticks:Int) { spawner.delay = ticks }
    fun getDelay() = spawner.delay

    fun setMinDelay(ticks:Int){ spawner.minSpawnDelay = ticks }
    fun getMinDelay() = spawner.minSpawnDelay

    fun setMaxDelay(ticks:Int){ spawner.maxSpawnDelay = ticks }
    fun getMaxDelay() = spawner.maxSpawnDelay

    fun setMaxSpawn(count:Int){ spawner.spawnCount = count }
    fun getMaxSpawn() = spawner.spawnCount

    fun setMaxNearBySpawn(count:Int){ spawner.maxNearbyEntities = count }
    fun getMaxNearBySpawn() = spawner.maxNearbyEntities


}