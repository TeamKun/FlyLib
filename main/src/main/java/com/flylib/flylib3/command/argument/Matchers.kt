package com.flylib.flylib3.command.argument

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType

@FunctionalInterface
abstract class Matcher<T> {
    abstract fun parse(arg: String): T?

    companion object {
        fun <T> of(lambda: (String) -> T?): Matcher<T> {
            return object : Matcher<T>() {
                override fun parse(arg: String): T? {
                    return lambda(arg)
                }
            }
        }
    }
}

// This class must implement toString()
// and the matcher must be able to parse the string
interface Matchable<T> {
    val matcher: Matcher<T>
}


abstract class TypeMatcher<T : Any> : Matcher<T>() {
    companion object {
        val all: MutableList<() -> TypeMatcher<out Any>> =
            mutableListOf(
                { IntTypeMatcher() },
                { FloatTypeMatcher() },
                { DoubleTypeMatcher() },
                { LongTypeMatcher() },
                { StringTypeMatcher() }
            )

        /**
         * @note If you made your own type/typeMatcher,You need to Register That
         * @note You can use lambda expression into FCommandBuilder.part(HERE)
         */
        fun register(matcher: () -> TypeMatcher<out Any>) {
            all.add(matcher)
        }

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

    fun getAsLambda(): (String) -> T? = { parse(it) }
}

class IntTypeMatcher : TypeMatcher<Int>() {
    override fun parse(arg: String): Int? {
        return arg.toIntOrNull()
    }

    override val type: KClass<Int> = Int::class
}

class FloatTypeMatcher : TypeMatcher<Float>() {
    override fun parse(arg: String): Float? {
        return arg.toFloatOrNull()
    }

    override val type: KClass<Float> = Float::class
}

class DoubleTypeMatcher : TypeMatcher<Double>() {
    override fun parse(arg: String): Double? {
        return arg.toDoubleOrNull()
    }

    override val type: KClass<Double> = Double::class
}

class LongTypeMatcher : TypeMatcher<Long>() {
    override fun parse(arg: String): Long? {
        return arg.toLongOrNull()
    }

    override val type: KClass<Long> = Long::class
}

class StringTypeMatcher : TypeMatcher<String>() {
    override fun parse(arg: String): String {
        return arg
    }

    override val type: KClass<String> = String::class
}

class LazyTypeMatcher<T : Any>(val lazyParser: (String) -> T?, val kClass: KClass<T>) : TypeMatcher<T>() {
    override fun parse(arg: String): T? {
        return lazyParser(arg)
    }

    override val type: KClass<T> = kClass
}