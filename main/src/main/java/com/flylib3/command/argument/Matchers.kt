package com.flylib3.command.argument

import com.flylib3.command.FCommandBuilderPart
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

abstract class Matcher<T> {
    /**
     * @return if the arg is correct
     */
    abstract fun isMatch(arg: String): Boolean

    abstract fun parse(arg: String): T?
}

//class combinationMatcher<T>(val matcher: List<Matcher<T>>) : Matcher<T>() {
//    override fun isMatch(arg: String): Boolean {
//        return matcher.all { it.isMatch(arg) }
//    }
//
//    override fun parse(arg: String): T? {
//
//    }
//}

abstract class TypeMatcher<T : Any> : Matcher<T>() {
    companion object {
        val all =
            listOf({ IntTypeMatcher() }, { FloatTypeMatcher() }, { DoubleTypeMatcher() }, { LongTypeMatcher() })

        fun getTypeMatcher(vararg str: String): List<TypeMatcher<out Any>> {
            return all.map { it() }.filter { parser -> str.all { parser.parse(it) != null } }
        }

        fun getTypeMatcher(kType: KType): TypeMatcher<out Any>? {
            return all.map { it() }.firstOrNull { it.type.createType() == kType }
        }

        fun getTypeMatcherForce(kType: KType): TypeMatcher<out Any> {
            val t = getTypeMatcher(kType)
            if (t == null) {
                TODO("KType:${kType} is not registered to TypeMatcher")
            } else {
                return t
            }
        }
    }

    abstract val type: KClass<T>
}

class IntTypeMatcher : TypeMatcher<Int>() {
    override fun isMatch(arg: String): Boolean = arg.toIntOrNull() != null
    override fun parse(arg: String): Int? {
        return arg.toIntOrNull()
    }

    override val type: KClass<Int> = Int::class
}

class FloatTypeMatcher : TypeMatcher<Float>() {
    override fun isMatch(arg: String): Boolean = arg.toFloatOrNull() != null
    override fun parse(arg: String): Float? {
        return arg.toFloatOrNull()
    }

    override val type: KClass<Float> = Float::class
}

class DoubleTypeMatcher : TypeMatcher<Double>() {
    override fun isMatch(arg: String): Boolean = arg.toDoubleOrNull() != null
    override fun parse(arg: String): Double? {
        return arg.toDoubleOrNull()
    }

    override val type: KClass<Double> = Double::class
}

class LongTypeMatcher : TypeMatcher<Long>() {
    override fun isMatch(arg: String): Boolean = arg.toLongOrNull() != null
    override fun parse(arg: String): Long? {
        return arg.toLongOrNull()
    }

    override val type: KClass<Long> = Long::class
}