package com.flylib3.util

import com.flylib3.FlyLibPlugin
import com.flylib3.command.FCommandBuilder


fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit) {
    FCommandBuilder.command(flylib, commandName, alias, lambda)
}