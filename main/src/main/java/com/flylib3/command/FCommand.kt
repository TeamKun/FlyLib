package com.flylib3.command

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.command.argument.TypeMatcher
import com.flylib3.event.ex.FCommandEvent
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.lang.Exception
import kotlin.reflect.KCallable
import kotlin.reflect.full.createType

abstract class FCommand : CommandExecutor, TabCompleter, UsageProvider {
    abstract val name: String
    abstract val alias: List<String>

    abstract fun permission(sender: CommandSender): Boolean
    abstract fun permissionMessage(sender: CommandSender)
}

class FCommandBuilder(
    flyLib: FlyLib,
    val commandName: String,
    val alias: List<String> = listOf()
) : FlyLibComponent(flyLib) {
    companion object {
        fun command(
            flyLib: FlyLib,
            commandName: String,
            alias: List<String> = listOf(),
            lambda: FCommandBuilder.() -> Unit
        ) {
            val builder = FCommandBuilder(flyLib, commandName, alias)
            lambda(builder)
            builder.registerAll()
        }
    }

    val partCommands = mutableListOf<BuiltFPathCommand>()

    fun <T : Any> part(vararg t: T, lambda: FCommandBuilderPart<T>.() -> Unit) {
        val root = FCommandBuilderPart<T>(null, flyLib, this, *t)
        lambda(root)
    }

    private fun registerAll() {
        // TODO Register Command
    }
}

class FCommandBuilderPart<T : Any>(
    val parent: FCommandBuilderPart<*>?,
    flyLib: FlyLib,
    val values: List<T>,
    val builder: FCommandBuilder
) :
    FlyLibComponent(flyLib) {
    constructor(parent: FCommandBuilderPart<*>?, flyLib: FlyLib, builder: FCommandBuilder, vararg values: T) : this(
        parent,
        flyLib,
        listOf(*values),
        builder
    )

    init {
        require(values.isNotEmpty())
    }

    val tType = values[0]::class.createType()
    val typeMatcher = TypeMatcher.getTypeMatcherForce(tType)
    fun <R : Any> part(vararg r: R, lambda: FCommandBuilderPart<R>.() -> Unit) {
        val entry = FCommandBuilderPart<R>(this, flyLib, builder, *r)
        lambda(entry)
    }

    fun terminal(lambda: FCommandBuilderPath.() -> Unit) {
        val path = FCommandBuilderPath(this, flyLib, builder)
        lambda(path)
        path.register(builder.commandName, *(builder.alias).toTypedArray())
    }
}

class FCommandBuilderPath(val bottom: FCommandBuilderPart<*>, flyLib: FlyLib, val builder: FCommandBuilder) :
    FlyLibComponent(flyLib) {
    fun getAll(): List<FCommandBuilderPart<*>> {
        val list = mutableListOf<FCommandBuilderPart<*>>()
        var e: FCommandBuilderPart<*>? = null
        do {
            if (e == null) {
                e = bottom
            } else {
                e = e.parent
            }

            if (e != null) {
                list.add(e)
            }
        } while (e != null)
        return list.reversed()
    }

    var usageString = listOf<String>()
        private set
    var permissionLambda: (CommandSender) -> Boolean = { true }
        private set
    var executor: KCallable<Boolean>? = null
        private set

    fun usage(vararg str: String) {
        usageString = str.toMutableList()
    }

    fun permission(lambda: (CommandSender) -> Boolean) {
        permissionLambda = lambda
    }

    fun execute(function: KCallable<Boolean>) {
        checkFunction(function)
        executor = function
    }

    private fun checkFunction(function: KCallable<Boolean>) {
        val params = function.parameters
        if (params[0].type != FCommandEvent::class.createType()) {
            // the first param is not command event type
            throw FCommandBuilderTypeException("Function:${function.name} whose first param is not FCommandEvent")
        }
        val types = getAll().map { it.tType }
        params.forEachIndexed { index, kParameter ->
            if (index == 0) return@forEachIndexed
            if (kParameter.type != types[(index - 1)]) {
                throw FCommandBuilderTypeException("Function:${function.name} whose index:${index} param is not appropriate")
            }
        }
    }

    internal fun register(commandName: String, vararg alias: String) {
        val all = this.getAll()
        val command = BuiltFPathCommand(all, this, commandName, *alias)
        builder.partCommands.add(command)
    }
}

open class FCommandBuilderException(msg: String) : Exception(msg)
class FCommandBuilderTypeException(msg: String) : FCommandBuilderException(msg)

class BuiltFPathCommand(
    val parts: List<FCommandBuilderPart<*>>,
    val fCommandBuilderPath: FCommandBuilderPath,
    commandName: String,
    vararg alias: String
) :
    FCommand() {
    private fun isMatch(part: FCommandBuilderPart<*>, str: String): Boolean {
        return part.values.any { part.typeMatcher.isMatch(str) }
    }

    private fun isMatchAll(args: Array<out String>): Boolean {
        if (args.size != parts.size) {
            return false
        }
        for (i in 0..args.lastIndex) {
            if (!isMatch(parts[i], args[i])) {
                return false
            }
        }
        return true
    }

    override val name: String = commandName
    override val alias: List<String> = alias.toList()

    override fun permission(sender: CommandSender): Boolean {
        return fCommandBuilderPath.permissionLambda(sender)
    }

    override fun permissionMessage(sender: CommandSender) {
        sender.sendMessage("" + ChatColor.RED + "You don't have enough permission")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return if (isMatchAll(args)) {
            execute(sender, command, label, args)
            true
        } else {
            false
        }
    }

    private fun execute(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (fCommandBuilderPath.executor != null) {
            val e = fCommandBuilderPath.executor!!
            val translated = mutableListOf<Any?>()
            for (i in 0..args.lastIndex) {
                translated.add(parts[i].typeMatcher.parse(args[i]))
            }
            val translatedNotNull = translated.filterNotNull()
            if (translated.size != translatedNotNull.size) {
                // Some Argument is not appropriate
                return false
            }
            return e.call(*translatedNotNull.toTypedArray())
        } else {
            // Executor Not Set
            return true
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (args.size >= parts.size) {
            // This provides nothing
            return null
        }
        for (i in 0..args.lastIndex) {
            if (!isMatch(parts[i], args[i])) {
                return null
            }
        }
        return parts[args.lastIndex].values.map { it.toString() }.toMutableList()
    }

    override fun usage(): Usage {
        TODO()
    }
}