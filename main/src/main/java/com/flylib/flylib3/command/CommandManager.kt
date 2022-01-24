package com.flylib.flylib3.command

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent

class CommandManager(override val flyLib: FlyLib) : FlyLibComponent {
    val commands = mutableListOf<FCommand>()

    fun register(command: FCommand) {
        val c = flyLib.plugin.getCommand(command.name)
        if (c == null) {
            // Not in Plugin Description File
            // TODO Auto Write and Add Statements of Command,then save it and reboot the server
//            flyLib.plugin.description.commands
        } else {
            commands.add(command)
            c.setExecutor(command)
            c.tabCompleter = command
            // TODO Register Usage Help Command
        }
    }
}