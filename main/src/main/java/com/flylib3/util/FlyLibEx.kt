package com.flylib3.util

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.FlyLibPlugin
import com.flylib3.command.FCommandBuilder
import com.flylib3.item.ItemData
import com.flylib3.item.buildItem
import com.flylib3.task.FRunnableContext
import org.bukkit.Material


fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit) {
    FCommandBuilder.command(flyLib, commandName, alias, lambda)
}

fun <T> FlyLibPlugin.everyTick(l: FRunnableContext.(Unit) -> T) = flyLib.task.everyTick(l)
fun <T> FlyLibPlugin.nextTick(l: FRunnableContext.(Unit) -> T) = flyLib.task.nextTick(l)
fun <T> FlyLibPlugin.later(l: FRunnableContext.(Unit) -> T, delay: Long) = flyLib.task.later(l, delay)
fun <T> FlyLibPlugin.task(l: FRunnableContext.(Unit) -> T) = flyLib.task.task(l)

fun FlyLibComponent.fine(str: String) = this.flyLib.log.fine(str)
fun FlyLibComponent.fine(lazy: (String) -> String) = this.flyLib.log.fine(lazy)

fun FlyLibComponent.info(str: String) = this.flyLib.log.info(str)
fun FlyLibComponent.info(lazy: (String) -> String) = this.flyLib.log.info(lazy)

fun FlyLibComponent.warn(str: String) = this.flyLib.log.warn(str)
fun FlyLibComponent.warn(lazy: (String) -> String) = this.flyLib.log.warn(lazy)

fun FlyLibComponent.error(str: String) = this.flyLib.log.error(str)
fun FlyLibComponent.error(lazy: (String) -> String) = this.flyLib.log.error(lazy)