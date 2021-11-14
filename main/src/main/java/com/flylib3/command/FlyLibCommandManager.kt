package com.flylib3.command

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent

class FlyLibCommandManager(flyLib: FlyLib) : FlyLibComponent(flyLib) {
    val commands = mutableListOf<Command>()

    fun register(command: Command) {
        val c = flyLib.plugin.getCommand(command.commandName())
        if (c == null) {
            // Not in Plugin Description File
            flyLib.plugin.description.commands
        }
    }
}