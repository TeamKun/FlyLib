package net.kunmc.flylib2.utils

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * EasyItemBuilder
 *
 * Usage:
 *
 * genItem(Material.BEEF,10,"Cooked Beef",listOf("That's a lie!"))
 *
 * ね？簡単でしょ？
 */
class EasyItemBuilder{
    companion object{
        fun genItem(material:Material,amount:Int = 1) = ItemStack(material,amount)
        fun genItem(material:Material,amount:Int = 1,name:String) = ItemStack(material,amount).setName(name)
        fun genItem(material:Material,amount:Int = 1,lore:List<String>) = ItemStack(material,amount).lore(lore)
        fun genItem(material:Material,amount:Int = 1,name:String,lore:List<String>) = ItemStack(material,amount).setName(name).lore(lore)
    }
}

fun ItemStack.setName(name:String):ItemStack{
    val meta = itemMeta
    meta.displayName(Component.text(name))
    itemMeta = meta
    return this
}

fun ItemStack.lore(lore:List<String>):ItemStack{
    this.lore(lore.map { Component.text(it) })
    return this
}

// End EasyItemBuilder

/**
 * SpawnEggUtil
 *
 * そこそこの確率では動いてくれるはず...
 */
class SpawnEggUtil{
    companion object{
        fun getEntityType(stack: ItemStack): EntityType? {
            val mobName = stack.type.name.replace("_SPAWN_EGG", "").toLowerCase()
            return EntityType.fromName(mobName)
        }

        fun getSpawnMaterial(type: EntityType): Material? {
            val itemName = type.name + "_SPAWN_EGG"
            return Material.getMaterial(itemName)
        }
    }
}

// End SpawnEggUtil

class EasySpawner {
    companion object {
        /**
         * @return if Success,return the block or not,return null
         */
        fun gen(type:EntityType,loc:Location): Block? {
            loc.block.type = Material.SPAWNER
            val meta = loc.block.state
            if(meta is CreatureSpawner){
                return loc.block
            }
            return null
        }

        @Deprecated("Magic Value Uses")
        fun gen(name:String,loc:Location): Block? {
            val type = EntityType.fromName(name)
            if(type != null) return gen(type,loc)
            return null
        }
    }
}

// End EasySpawner
/**
 * ItemMarker
 *
 * Usage:
 *
 * val stack1:ItemStack
 * val marker1 = ItemMarker(stack1,plugin)
 *
 * marker1.isMatched(stack2) // False
 * marker1.isMatched(stack1) // True
 */
class ItemMarker(private val itemStack:ItemStack,private val plugin:JavaPlugin){
    private val id = UUID.randomUUID()
    init {
        mark(itemStack,id.toString(),"ItemMarker",plugin)
    }

    fun isMatched(other:ItemStack): Boolean {
        val s = get(other,"ItemMarker",plugin) ?: return false
        return s === id.toString()
    }

    companion object{
        /**
         * Write Data To ItemStack's PersistentDataContainer
         */
        fun mark(stack:ItemStack,data:String,key:String,plugin:JavaPlugin){
            val m = stack.itemMeta
            val nameKey = NamespacedKey(plugin,key)
            m.persistentDataContainer.set(nameKey, PersistentDataType.STRING,data)
        }

        fun get(stack:ItemStack,key:String,plugin:JavaPlugin): String? {
            val m = stack.itemMeta
            val nameKey = NamespacedKey(plugin,key)
            return m.persistentDataContainer.get(nameKey, PersistentDataType.STRING)
        }
    }
}

// End ItemMarker

class CommonItemSet{
    companion object{
        fun playerHead(p:Player): ItemStack? {
            val stack = EasyItemBuilder.genItem(Material.PLAYER_HEAD,name = p.displayStringName())
            val meta = stack.itemMeta
            return if(meta is SkullMeta){
                meta.owningPlayer = p
                stack.itemMeta = meta
                stack
            }else{
                null
            }
        }

        fun playerHeads(): List<ItemStack> {
            return Bukkit.getOnlinePlayers().mapNotNull { playerHead(it) }
        }
    }
}