package com.flylib3.item

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.command.argument.Matchable
import com.flylib3.command.argument.Matcher
import com.flylib3.util.dataContainer
import com.flylib3.util.info
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ItemStackManager(override val flyLib: FlyLib) : FlyLibComponent {
    companion object {
        val allPersistentDataType = mutableListOf<PersistentDataType<*, *>>(
            // Paper Start
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
            // Paper End
            // FlyLib Start
            // For List Methods, see ListPersistentDataType
            StringPersistentDataType(Byte::class.javaObjectType, Matcher.of { it.toByteOrNull() }),
            StringPersistentDataType(Double::class.javaObjectType, Matcher.of { it.toDoubleOrNull() }),
            StringPersistentDataType(Float::class.javaObjectType, Matcher.of { it.toFloatOrNull() }),
            StringPersistentDataType(Int::class.javaObjectType, Matcher.of { it.toIntOrNull() }),
            StringPersistentDataType(Long::class.javaObjectType, Matcher.of { it.toLongOrNull() }),
            StringPersistentDataType(Short::class.javaObjectType, Matcher.of { it.toShortOrNull() }),
        )
    }

    val namespace = NamespacedKey(flyLib.plugin, "flylib")

    /**
     * To Register a new PersistentDataType
     * @param type PersistentDataType
     */
    fun registerPersistentDataType(type: PersistentDataType<*, *>) {
        allPersistentDataType.add(type)
    }

    inline fun <reified V : Any> get(stack: ItemStack): V? {
        val dataType = persistentDataType(V::class.java)
        if (dataType == null) {
            return null
        } else {
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

    inline fun <reified T : Matchable<T>> getMatchable(stack: ItemStack, matcher: Matcher<T>): T? {
        val dataType = MatchablePersistentDataType(T::class.java, matcher)
        return stack.dataContainer {
            return@dataContainer it[namespace, dataType]
        }
    }

    inline fun <reified T : Matchable<T>> setMatchable(stack: ItemStack, matcher: Matcher<T>, value: T): Boolean {
        val dataType = MatchablePersistentDataType(T::class.java, matcher)
        stack.dataContainer {
            it[namespace, dataType] = value
        }
        return true
    }

    inline fun <reified V : Any> getList(stack: ItemStack): List<V>? {
        val dataType = listPersistentDataType<V>()
        if (dataType == null) {
            return null
        } else {
            return stack.dataContainer {
                return@dataContainer it[namespace, dataType]
            }
        }
    }

    inline fun <reified V : Any> setList(stack: ItemStack, list: List<V>): Boolean {
        val dataType = listPersistentDataType<V>() ?: return false
        val result = stack.dataContainer {
            it[namespace, dataType] = list
        }
        return result != null
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
        } as PersistentDataType<T, Z>?  // Checked cast
    }

    fun <V> persistentDataType(
        vClass: Class<V>
    ): PersistentDataType<*, V>? {
        return allPersistentDataType.firstOrNull {
            // Check ComplexType is same as vClass or the super class of vClass
            it.complexType == vClass || it.complexType.isAssignableFrom(vClass)
        } as PersistentDataType<*, V>?  // Checked cast
    }

    /**
     * @return true if tClass is the subclass of list
     */
    fun <T> isList(tClass: Class<T>): Boolean {
        return tClass == List::class.java || tClass.isAssignableFrom(List::class.java)
    }

    /**
     * @param tClass the subclass of list
     */
    fun <T> getListGenerics(tClass: Class<T>): Type {
        val type = tClass.genericSuperclass as ParameterizedType
        val types = type.actualTypeArguments
        return types[0]
    }

    inline fun <reified T : Any> listPersistentDataType(): PersistentDataType<String, List<T>>? {
        info { "${it}listPersistentDataType: ${T::class.java}" }
        val tDataType = persistentDataType(String::class.java, T::class.java) ?: return null
        info { "${it}tDataType: $tDataType" }
        info { "${it}tDataType.primitiveType: ${tDataType.primitiveType}" }
        return if (tDataType.primitiveType == String::class.java) {
            ListPersistentDataType<T>(T::class.java, tDataType as PersistentDataType<String, T>) // Checked cast
        } else {
            null
        }
    }
}

abstract class ObjectPersistentDataType<T, Z>(val primitive: Class<T>, val complex: Class<Z>) :
    PersistentDataType<T, Z> {
    abstract override fun toPrimitive(complex: Z, context: PersistentDataAdapterContext): T
    abstract override fun fromPrimitive(primitive: T, context: PersistentDataAdapterContext): Z
    override fun getPrimitiveType(): Class<T> = primitive
    override fun getComplexType(): Class<Z> = complex
}

/**
 * @param clx the class of the complex type
 * @param matcher the matcher to match the complex type
 */
class MatchablePersistentDataType<T>(val clx: Class<T>, val matcher: Matcher<T>) :
    ObjectPersistentDataType<String, T>(String::class.java, clx) {
    override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): String {
        return complex.toString()
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): T {
        return matcher.parse(primitive)!!
    }
}

class ListPersistentDataType<T : Any>(
    val clx: Class<T>,
    val persistentDataType: PersistentDataType<String, T>,
    val separator: String = "LPS"
) :
    ObjectPersistentDataType<String, List<T>>(String::class.java, listOf<T>().javaClass) {
    override fun toPrimitive(complex: List<T>, context: PersistentDataAdapterContext): String {
        return complex.joinToString(separator) {
            persistentDataType.toPrimitive(it, context)
        }
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): List<T> {
        return primitive.split(separator).map {
            persistentDataType.fromPrimitive(it, context)
        }
    }
}

class StringPersistentDataType<T>(val tClass: Class<T>, val matcher: Matcher<T>) : ObjectPersistentDataType<String, T>(
    String::class.java,
    tClass
) {
    override fun toPrimitive(complex: T, context: PersistentDataAdapterContext): String {
        return complex.toString()
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): T {
        return matcher.parse(primitive)!!
    }
}