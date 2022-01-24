package com.flylib.flylib3.util

import com.flylib.flylib3.FlyLibComponent
import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.command.FCommandBuilder
import com.flylib.flylib3.task.FRunnableContext
import org.bukkit.event.Event
import kotlin.reflect.KClass


fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit) {
    FCommandBuilder.command(flyLib, commandName, alias, lambda)
}

fun <T> FlyLibComponent.everyTick(l: FRunnableContext.(Unit) -> T) = flyLib.task.everyTick(l)
fun <T> FlyLibComponent.nextTick(l: FRunnableContext.(Unit) -> T) = flyLib.task.nextTick(l)
fun <T> FlyLibComponent.later(delay: Long, l: FRunnableContext.(Unit) -> T) = flyLib.task.later(l, delay)
fun <T> FlyLibComponent.task(l: FRunnableContext.(Unit) -> T) = flyLib.task.task(l)
fun <T : Event, R> FlyLibComponent.event(event: KClass<T>, l: FRunnableContext.(T) -> R) = flyLib.task.event(event, l)
inline fun <reified T : Event, R> FlyLibComponent.event(noinline l: FRunnableContext.(T) -> R) =
    flyLib.task.event<T, R>(l)

fun FlyLibComponent.fine(str: String) = this.flyLib.log.fine(str)
fun FlyLibComponent.fine(lazy: (String) -> String) = this.flyLib.log.fine(lazy)

fun FlyLibComponent.info(str: String) = this.flyLib.log.info(str)
fun FlyLibComponent.info(lazy: (String) -> String) = this.flyLib.log.info(lazy)

fun FlyLibComponent.warn(str: String) = this.flyLib.log.warn(str)
fun FlyLibComponent.warn(lazy: (String) -> String) = this.flyLib.log.warn(lazy)

fun FlyLibComponent.error(str: String) = this.flyLib.log.error(str)
fun FlyLibComponent.error(e: Exception) = this.flyLib.log.error(e)
fun FlyLibComponent.error(lazy: (String) -> String) = this.flyLib.log.error(lazy)

var flyLibLogging = true

/**
 * For Internal Log
 */
internal fun FlyLibComponent.log(str: String) {
    if (flyLibLogging) {
        info { "[FlyLib-Internal-Log]$str" }
    }
}

fun FlyLibComponent.ready(lambda: () -> Unit) = flyLib.onReady(lambda)