package com.flylib3.command

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

abstract class Command(flyLib: FlyLib) : CommandExecutor, TabCompleter, FlyLibComponent(flyLib) {
    abstract override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean

    abstract override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>?

    /**
     * @return true if the sender is allowed to execute this command
     */
    abstract fun permission(sender: CommandSender): Boolean

    open fun permissionMessage(sender: CommandSender) {
        sender.sendMessage("Not Enough Permission Error")
    }

    abstract fun usage(): String

    abstract fun commandName(): String
}