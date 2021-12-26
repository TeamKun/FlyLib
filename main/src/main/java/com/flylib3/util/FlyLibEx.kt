package com.flylib3.util

import com.flylib3.FlyLib
import com.flylib3.FlyLibPlugin
import com.flylib3.command.FCommandBuilder


fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit) {
    FCommandBuilder.command(flylib, commandName, alias, lambda)
}

fun <T> FlyLibPlugin.everyTick(l: FlyLib.(Unit) -> T) = flylib.task.everyTick(l)
fun <T> FlyLibPlugin.nextTick(l: FlyLib.(Unit) -> T) = flylib.task.nextTick(l)
fun <T> FlyLibPlugin.later(l: FlyLib.(Unit) -> T, delay: Long) = flylib.task.later(l, delay)