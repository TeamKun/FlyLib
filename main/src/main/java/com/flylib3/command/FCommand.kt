package com.flylib3.command

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.command.argument.TypeMatcher
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.util.allIndexed
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import kotlin.Exception
import kotlin.reflect.KCallable
import kotlin.reflect.KType
import kotlin.reflect.full.createType

abstract class FCommand : CommandExecutor, TabCompleter, UsageProvider {
    abstract val name: String
    abstract val alias: List<String>
    abstract fun permission(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    abstract fun permissionMessage(sender: CommandSender, command: Command, label: String, args: Array<out String>)
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
            val builder = FCommandBuilder(flyLib, commandName.lowercase(), alias)
            lambda(builder)
            builder.registerAll()
        }
    }

    val partCommands = mutableListOf<BuiltFPathCommand>()

    fun <T : Any> part(vararg t: T, lambda: FCommandBuilderPart<T>.() -> Unit) {
        val root = FCommandBuilderPart<T>(null, flyLib, this, *t)
        lambda(root)
    }

    fun <T : Any> part(
        type: KType,
        lazyValues: (
            CommandSender,
            Command,
            String,
            Array<out String>
        ) -> List<T>,
        lazyParser: (String) -> T?,
        lambda: FCommandBuilderPart<T>.() -> Unit
    ) {
        val root = FCommandBuilderPart<T>(null, flyLib, lazyValues, lazyParser, this, type)
        lambda(root)
    }

    private fun exportIntoOne(): BuiltFCommand {
        val commands = partCommands.toMutableList()
        return BuiltFCommand(*commands.toTypedArray())
    }

    private fun registerAll() {
        val exported = exportIntoOne()
        flyLib.command.register(exported)
    }
}

/**
 * @param tType KType of Generics
 * @param lazyValues Array<out String> is the arguments "BEFORE" this part
 */
class FCommandBuilderPart<T : Any>(
    val parent: FCommandBuilderPart<*>?,
    flyLib: FlyLib,
    val lazyValues: (
        CommandSender,
        Command,
        String,
        Array<out String>
    ) -> List<T>,
    val lazyParser: (String) -> T?,
    val builder: FCommandBuilder,
    val tType: KType
) :
    FlyLibComponent(flyLib) {
    constructor(parent: FCommandBuilderPart<*>?, flyLib: FlyLib, builder: FCommandBuilder, vararg values: T) : this(
        parent,
        flyLib,
        listOf(*values),
        builder
    )

    constructor(parent: FCommandBuilderPart<*>?, flyLib: FlyLib, values: List<T>, builder: FCommandBuilder) : this(
        parent,
        flyLib,
        lazyValues = { a, b, c, d -> values },
        (TypeMatcher.getTypeMatcherForce(values[0]::class.createType()) as TypeMatcher<T>).getAsLambda(), // Checked
        builder,
        values[0]::class.createType()
    )

    fun <R : Any> part(vararg r: R, lambda: FCommandBuilderPart<R>.() -> Unit) {
        val entry = FCommandBuilderPart<R>(this, flyLib, builder, *r)
        lambda(entry)
    }

    fun <R : Any> part(
        type: KType,
        lazyValues: (
            CommandSender,
            Command,
            String,
            Array<out String>
        ) -> List<R>,
        lazyParser: (String) -> R?,
        lambda: FCommandBuilderPart<R>.() -> Unit
    ) {
        val root = FCommandBuilderPart<R>(this, flyLib, lazyValues, lazyParser, builder, type)
        lambda(root)
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
    var permissionLambda: (CommandSender, Command, String, Array<out String>) -> Boolean = { _, _, _, _ -> true }
        private set
    var executor: KCallable<Boolean>? = null
        private set

    fun usage(vararg str: String) {
        usageString = str.toMutableList()
    }

    fun permission(lambda: (CommandSender, Command, String, Array<out String>) -> Boolean) {
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
    private fun <T : Any> isMatch(
        part: FCommandBuilderPart<T>,
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
        str: String
    ): Boolean {
        try {
            val t: T? = part.lazyParser(str)
            if (t == null) {
                return false
            } else {
                // Type is Same
                return part.lazyValues(sender, command, label, args).contains(t)
            }
        } catch (e: Exception) {
            // Something happened in Parsing String
            return false
        }
    }

    private fun <T : Any> isPartlyMatch(
        part: FCommandBuilderPart<T>,
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>,
        str: String
    ): Boolean {
        try {
            val t: T? = part.lazyParser(str)
            if (t == null) {
                return false
            } else {
                // Type is Same
                return part.lazyValues(sender, command, label, args).any { it.toString().startsWith(str) }
            }
        } catch (e: Exception) {
            // Something happened in Parsing String
            return false
        }
    }

    fun isMatchAll(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.size != parts.size) {
            return false
        }
        for (i in 0..args.lastIndex) {
            if (!isMatch(parts[i], sender, command, label, getArgsBeforeIndexPart(args, i), args[i])) {
                return false
            }
        }
        return true
    }

    override val name: String = commandName
    override val alias: List<String> = alias.toList()

    override fun permission(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return fCommandBuilderPath.permissionLambda(sender, command, label, args)
    }

    override fun permissionMessage(sender: CommandSender, command: Command, label: String, args: Array<out String>) {
        sender.sendMessage("" + ChatColor.RED + "You don't have enough permission")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!permission(sender, command, label, args)) {
            permissionMessage(sender, command, label, args)
            return true
        }
        return if (isMatchAll(sender, command, label, args)) {
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
                translated.add(parts[i].lazyParser(args[i]))
            }
            val translatedNotNull = translated.filterNotNull()
            if (translated.size != translatedNotNull.size) {
                // Some Argument is not appropriate
                return false
            }
            return e.call(FCommandEvent(sender, fCommandBuilderPath), *translatedNotNull.toTypedArray())
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
        if (args.isEmpty()) {
            // Only Command Name
            return parts[0].lazyValues(sender, command, alias, arrayOf()).map { it.toString() }.toMutableList()
        } else {
            // Excluded for Command Label and Last one
            val argsWithoutLast = args.slice(0 until args.lastIndex)
            if (argsWithoutLast.allIndexed { s, i ->
                    isMatch(
                        parts[i],
                        sender,
                        command,
                        alias,
                        getArgsBeforeIndexPart(args, i),
                        s
                    )
                }) {
                // Except for Last one,all matched
                if (isMatch(
                        parts[args.lastIndex],
                        sender,
                        command,
                        alias,
                        getArgsBeforeIndexPart(args, args.lastIndex),
                        args.last()
                    )
                ) {
                    // The last one is also match
                    // return the next one if exist
                    if (parts.lastIndex >= args.lastIndex + 1) {
                        return parts[args.lastIndex + 1].lazyValues(sender, command, alias, args)
                            .map { it.toString() }.toMutableList()
                    } else {
                        // Not Exist
                        return null
                    }
                } else {
                    // Not Last one is matched
                    // Return filtered values
                    return parts[args.lastIndex].lazyValues(sender, command, alias, args)
                        .map { it.toString() }.filter { it.startsWith(args.last()) }.toMutableList()
                }
            } else {
                // Not Matched even except for last one
                return null
            }
        }
    }

    private fun getArgsBeforeIndexPart(args: Array<out String>, index: Int): Array<String> {
        return if (index == 0)
            arrayOf<String>()
        else
            args.toMutableList().slice(0 until index).toTypedArray()
    }

    override fun usage(): Usage {
        TODO()
    }
}

class BuiltFCommand(vararg val command: BuiltFPathCommand) : FCommand() {
    init {
        require(command.none { command[0].name != it.name })
        require(command.none { command[0].alias != it.alias })
    }

    override val name: String = command[0].name
    override val alias: List<String> = command[0].alias

    override fun permission(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        return when (val matched = getMatched(sender, command, label, args)) {
            is BuiltFPathCommand -> {
                matched.permission(sender, command, label, args)
            }
            is BuiltFCommand -> {
                throw Exception("in BuiltFCommand#permission command path is duplicated")
            }
            null -> {
                false   // Nothing matched
            }
            else -> {
                throw Exception("in BuiltFCommand#permission not expected matched command")
            }
        }
    }

    override fun permissionMessage(sender: CommandSender, command: Command, label: String, args: Array<out String>) {
        when (val matched = getMatched(sender, command, label, args)) {
            is BuiltFPathCommand -> {
                matched.permissionMessage(sender, command, label, args)
            }
            is BuiltFCommand -> {
                throw Exception("in BuiltFCommand#permissionMessage command path is duplicated")
            }
            null -> {
                // None Matched
                return
            }
            else -> {
                throw Exception("in BuiltFCommand#permission not expected matched command")
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val matched = getMatched(sender, command, label, args)
        println("[Matched]:${matched}")
        return when (matched) {
            is BuiltFPathCommand -> {
                matched.onCommand(sender, command, label, args)
            }
            is BuiltFCommand -> {
                // Many is Matched
                return false
            }
            null -> {
                // None Matched
                return false
            }
            else -> {
                throw Exception("in BuiltFCommand#permission not expected matched command")
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return this.command.mapNotNull { it.onTabComplete(sender, command, alias, args) }.flatten().distinct()
            .toMutableList()
    }

    override fun usage(): Usage {
        TODO("Not yet implemented")
    }

    private fun getMatched(sender: CommandSender, command: Command, label: String, args: Array<out String>): FCommand? {
        val matched = this.command.filter { it.isMatchAll(sender, command, label, args) }
        if (matched.isEmpty()) {
//            println("None Matched")
            return null
        } else if (matched.size == 1) {
//            println("One:${matched[0]} Matched")
            return matched[0]
        } else {
//            println("${matched.size} Matched")
            return BuiltFCommand(*matched.toTypedArray())
        }
    }

    private fun isSingle(command: FCommand) = command is BuiltFPathCommand
}