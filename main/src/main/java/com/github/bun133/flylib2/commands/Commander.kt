package com.github.bun133.flylib2.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

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
        val invoker = builders.filter { it.getChain().any { c -> c.isAllMatch(ar) } }.firstOrNull()
            ?.getInvoker()
        if (invoker == null) {
            println("Not Matched")
            return false
        } else {
            return invoker(plugin, sender, args)
        }
    }

    fun register(alias: String) {
        plugin.getCommand(alias)!!.setExecutor(this)
        plugin.getCommand(alias)!!.tabCompleter = tab()
        plugin.getCommand(alias)!!.description = description
        plugin.getCommand(alias)!!.usage = usage
    }
}

class CommanderBuilder<T> {
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
}
