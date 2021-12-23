package com.flylib3.command

import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand

/**
 * Class That Provides Permission Check,Args Check etc.
 */
class CommandExecutorProxy(private val command: FCommand) : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (!this.command.permission(sender, command, label, args)) {
            // Sender don't have enough permission to execute
            this.command.permissionMessage(sender, command, label, args)
            return false
        }

        // TODO Arg Type Check System

        return this.command.onCommand(sender, command, label, args)
    }

    fun register(c: PluginCommand) {
        c.setExecutor(command)
        c.tabCompleter = command
    }
}