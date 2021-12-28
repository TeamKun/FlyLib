package com.flylib3.util

import com.flylib3.FlyLib
import com.flylib3.FlyLibPlugin
import com.flylib3.command.FCommandBuilder
import com.flylib3.task.FRunnableContext


fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit) {
    FCommandBuilder.command(flylib, commandName, alias, lambda)
}

fun <T> FlyLibPlugin.everyTick(l: FRunnableContext.(Unit) -> T) = flylib.task.everyTick(l)
fun <T> FlyLibPlugin.nextTick(l: FRunnableContext.(Unit) -> T) = flylib.task.nextTick(l)
fun <T> FlyLibPlugin.later(l: FRunnableContext.(Unit) -> T, delay: Long) = flylib.task.later(l, delay)
fun <T> FlyLibPlugin.task(l:FRunnableContext.(Unit) -> T) = flylib.task.task(l)