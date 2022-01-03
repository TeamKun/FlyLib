package com.flylib3.item

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.util.dataContainer
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemStackManager(override val flyLib: FlyLib) : FlyLibComponent {
    companion object {
        val allPersistentDataType = mutableListOf<PersistentDataType<*, *>>(
            PersistentDataType.BYTE,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.DOUBLE,
            PersistentDataType.FLOAT,
            PersistentDataType.INTEGER,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.SHORT,
            PersistentDataType.STRING,
        )
    }

    val namespace = NamespacedKey(flyLib.plugin, "flylib")
    fun <T : Any, Z : Any> get(stack: ItemStack, type: PersistentDataType<T, Z>) =
        stack.dataContainer { return@dataContainer it[namespace, type] }

    fun <T : Any, Z : Any> set(stack: ItemStack, type: PersistentDataType<T, Z>, value: Z) =
        stack.dataContainer { it[namespace, type] = value }

    inline fun <reified V : Any> get(stack: ItemStack): V? {
        val dataType = persistentDataType(V::class.java)
        if (dataType == null) {
            return null
        }else{
            return stack.dataContainer {
                return@dataContainer it[namespace, dataType]
            }
        }
    }

    inline fun <reified V : Any> set(stack: ItemStack, value: V): Boolean {
        val dataType = persistentDataType(V::class.java) ?: return false
        stack.dataContainer {
            it[namespace, dataType] = value
        }
        return true
    }

    /**
     * @return null if not found persistentDataType
     */
    fun <T, Z> persistentDataType(
        tClass: Class<T>,
        zClass: Class<Z>
    ): PersistentDataType<T, Z>? {
        return allPersistentDataType.firstOrNull {
            it.primitiveType == tClass && it.complexType == zClass
        } as? PersistentDataType<T, Z>  // Checked cast
    }

    fun <V> persistentDataType(
        vClass: Class<V>
    ): PersistentDataType<*, V>? {
        return allPersistentDataType.firstOrNull {
            it.complexType == vClass
        } as? PersistentDataType<*, V>  // Checked cast
    }
}