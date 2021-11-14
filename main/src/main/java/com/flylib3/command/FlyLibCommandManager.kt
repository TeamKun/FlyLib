package com.flylib3.command

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent

class FlyLibCommandManager(flyLib: FlyLib) : FlyLibComponent(flyLib) {
    // Usually,All Commands will be called through Proxy
    // All Proxies are registered here
    val proxy = mutableListOf<CommandExecutorProxy>()

    fun register(command: Command) {
        val c = flyLib.plugin.getCommand(command.commandName())
        if (c == null) {
            // Not in Plugin Description File
            // TODO Auto Write and Add Statements of Command,then save it and reboot the server
//            flyLib.plugin.description.commands
        } else {
            val commandProxy = CommandExecutorProxy(command)
            commandProxy.register(c)
            proxy.add(commandProxy)
        }
    }

    fun register(command: (FlyLib) -> Command) = register(command(flyLib))
}