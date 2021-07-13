package com.github.bun133.flylib2.commands

import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import javax.print.DocFlavor

abstract class ExCommand : CommandExecutor {
    abstract fun tab(): TabCompleter
}

class Commander<T : JavaPlugin>(
    val plugin: T,
    var description: String,
    var usage: String,
    vararg builders: CommanderBuilder<T>
) : ExCommand() {
    private val builders = mutableListOf(*builders)

    private val tab: SmartTabCompleter = genTab()
    private fun genTab(): SmartTabCompleter {
        val chains = mutableListOf<TabChain>()
        builders.map { it.getChain() }.forEach { chains.addAll(it) }
        return SmartTabCompleter(chains)
    }

    override fun tab(): TabCompleter = tab

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val ar = arrayOf(*args)
        val allMatched = builders.filter { it.getChain().any { c -> c.isAllMatch(ar) } }
        if (allMatched.isEmpty()) {
            //そもそもの入力ミス
            return false
        } else {
            val filterResult = allMatched[0].getFilterResult(plugin, sender, args)
            if (filterResult) {
                val invoker = allMatched[0].getInvoker()
                if (invoker == null) {
                    println("[FlyLib-2]Command:${usage} has wrong invoker")
                    return false
                }
                return (invoker)(plugin, sender, args)
            } else {
                // 何かしらに引っかかった
                val message = allMatched[0].getFilterMessage(plugin, sender, args)
                if (message != null) {
                    sender.sendMessage(message)
                    // メッセージ送ったらusage送る必要なくない????
                    return true
                }
                return false
            }
        }


//        val invoker = builders.filter {
//            val b = it.getFilterResult(plugin, sender, args)
//            println("Filter:$b")
//            b
//        }.firstOrNull { it.getChain().any { c -> c.isAllMatch(ar) } }
//            ?.getInvoker()
//        if (invoker == null) {
//            println("Not Matched")
//            return false
//        } else {
//            return invoker(plugin, sender, args)
//        }
    }

    fun register(alias: String) {
        plugin.getCommand(alias)!!.setExecutor(this)
        plugin.getCommand(alias)!!.tabCompleter = tab()
        plugin.getCommand(alias)!!.description = description
        plugin.getCommand(alias)!!.usage = usage
    }
}

class CommanderBuilder<T : JavaPlugin> {
    private var chains: MutableList<TabChain> = mutableListOf()
    fun addTabChain(chain: TabChain): CommanderBuilder<T> {
        chains.add(chain)
        return this
    }

    fun addTabChain(vararg chain: TabChain): CommanderBuilder<T> {
        chains.addAll(chain)
        return this
    }

    fun getChain() = chains
    private var invoker: ((T, CommandSender, Array<out String>) -> Boolean)? = null
    fun setInvoker(invoker: (T, CommandSender, Array<out String>) -> Boolean): CommanderBuilder<T> {
        this.invoker = invoker
        return this
    }

    fun getInvoker() = invoker

    private var filters = mutableListOf<Filter<T>>()
    fun addFilter(filter: Filter<T>): CommanderBuilder<T> {
        filters.add(filter)
        return this
    }

    fun getFilters() = filters
    fun getFilterResult(plugin: T, sender: CommandSender, arg: Array<out String>): Boolean {
        val filters = getFilters()
        if (filters.isEmpty()) return true
        return !filters.any { !it.f(plugin, sender, arg) }
    }

    fun getFilterMessage(plugin: T, sender: CommandSender, arg: Array<out String>): String? {
        return if (getFilterResult(plugin, sender, arg)) null
        else getFilters().filter { !it.f(plugin, sender, arg) }[0].message(plugin, sender, arg)
    }

    open class Filter<T : JavaPlugin>(
        val f: (T, CommandSender, Array<out String>) -> Boolean,
        val message: (T, CommandSender, Array<out String>) -> String
    )

    class Filters {
        fun <T : JavaPlugin> filterOp() = OP<T>()
        fun <T : JavaPlugin> filterCreative() = Creative<T>()
        fun <T : JavaPlugin> filterNotPlayer() = NotPlayer<T>()
        class OP<T : JavaPlugin> : Filter<T>(
            { _, commandSender, _ -> commandSender is Player && commandSender.isOp || commandSender is BlockCommandSender || commandSender is ConsoleCommandSender },
            { t, s, arg -> "" + ChatColor.RED + "You don't have enough permission" }
        )

        class Creative<T : JavaPlugin> :
            Filter<T>(
                { _, commandSender, _ -> commandSender is Player && commandSender.gameMode === GameMode.CREATIVE },
                { t, s, arg -> "" + ChatColor.RED + "You aren't creative!" }
            )

        class NotPlayer<T : JavaPlugin> : Filter<T>(
            { _, commandSender, _ -> commandSender !is Player },
            { t, s, arg -> "" + ChatColor.RED + "This command can't be invoked by player" }
        )
    }
}