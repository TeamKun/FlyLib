package com.flylib3.command

import com.flylib3.FlyLib
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.lang.Exception

/**
 * Helper Class To Create Command
 */
class CommandBuilder(val flyLib: FlyLib) {
    private var executor: ((
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ) -> Boolean)? = null

    private var tabCompleter: ((
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ) -> MutableList<String>?)? = null

    private var permission: ((CommandSender) -> Boolean)? = null

    private var permissionMessage: ((CommandSender) -> Unit)? = null

    private var usage: String? = null

    private var commandName: String? = null

    fun execute(
        f: (
            sender: CommandSender,
            command: org.bukkit.command.Command,
            label: String,
            args: Array<out String>
        ) -> Boolean
    ): CommandBuilder {
        executor = f
        return this
    }

    fun tabComplete(
        f: (
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<out String>
        ) -> MutableList<String>?
    ): CommandBuilder {
        tabCompleter = f
        return this
    }

    fun permission(f: (CommandSender) -> Boolean): CommandBuilder {
        this.permission = f
        return this
    }

    fun permissionMessage(f: (CommandSender) -> Unit): CommandBuilder {
        this.permissionMessage = f
        return this
    }

    fun usage(s: String): CommandBuilder {
        this.usage = s
        return this
    }

    fun name(s: String) = commandName(s)
    fun commandName(s: String): CommandBuilder {
        this.commandName = s
        return this
    }

    fun build(): CommandBuilderCommand? {
        if (executor != null && tabCompleter != null && permission != null && permissionMessage != null && usage != null && commandName != null) {
            return CommandBuilderCommand(
                flyLib,
                executor!!,
                tabCompleter!!,
                permission!!,
                permissionMessage!!,
                usage!!,
                commandName!!
            )
        } else {
            return null
        }
    }

    fun buildForce(): CommandBuilderCommand {
        val c = build()
        if (c != null) {
            return c
        } else {
            val e = Exception("During CommandBuilder#buildForce,Failed to Build")
            flyLib.log.error(e)
            throw e
        }
    }
}

class CommandBuilderCommand(
    flyLib: FlyLib,
    val executor: ((
        sender: CommandSender,
        command: org.bukkit.command.Command,
        label: String,
        args: Array<out String>
    ) -> Boolean),
    val tabCompleter: (
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ) -> MutableList<String>?,
    val perm: (CommandSender) -> Boolean,
    val permMessage: ((CommandSender) -> Unit),
    val usage: String,
    val commandName: String
) : com.flylib3.command.Command(flyLib) {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return executor(sender, command, label, args)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        return tabCompleter(sender, command, alias, args)
    }

    override fun permission(sender: CommandSender): Boolean {
        return perm(sender)
    }

    override fun permissionMessage(sender: CommandSender) {
        permMessage.invoke(sender)
    }

    override fun usage(): String = usage

    override fun commandName(): String = commandName
}