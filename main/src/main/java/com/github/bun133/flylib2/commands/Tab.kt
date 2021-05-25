package com.github.bun133.flylib2.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.lang.IndexOutOfBoundsException

class SmartTabCompleter(vararg c: TabChain) : TabCompleter {
    constructor(c: MutableList<TabChain>) : this(){
        chains.addAll(c)
    }

    private val chains = mutableListOf(*c)
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val strings = args.toMutableList().map { it as String }.toTypedArray()
        val list = mutableListOf<String>()
        chains
            .filter { it.isExist(strings) }
            .forEach { chain ->
                chain.getMatched(strings)!!.getAsList()
                    // ADDED
                    .filter { it.startsWith(strings[strings.lastIndex]) }
                    .forEach { list.add(it) }
            }
        if (list.isEmpty()) list.add("")
        return list
    }
}

class TabChain(vararg obj: TabObject) {
    private val tabObjects = mutableListOf(*obj)
    fun getMatched(args: Array<String>): TabObject? {
        if (args.size - 1 > tabObjects.size) {
            return null
        }
        if (args.isEmpty()) {
            return tabObjects[0]
        }
        var isMatched = true
        tabObjects.forEachIndexed { index, tabObject ->
            if (index >= args.lastIndex) {
            } else {
                if (!tabObject.getAsList().contains(args[index])) {
                    isMatched = false
                }
            }
        }
        if (!isMatched) {
            return null
        } else {
            return try {
                tabObjects[args.lastIndex]
            } catch (e: IndexOutOfBoundsException) {
                null
            }
        }
    }

    fun isAllMatch(args:Array<String>):Boolean{
        if(args.size != tabObjects.size){
            println("tabObjects.size not matched")
            return false
        }
        args.forEachIndexed { index, s ->
            if(!tabObjects[index].isMatch(s)){
                println("TabObject index:$index ${tabObjects[index].toString()} not containing $s")
                return false
            }
        }
        return true
    }

    fun isExist(args: Array<String>) = getMatched(args) != null

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("(")
        for (obj in tabObjects) {
            builder.append("{${obj.toString()}},")
        }
        builder.append(")")
        return builder.toString()
    }
}

open class TabObject {
    constructor(vararg objs: TabObject) {
        obj.addAll(objs)
    }

    constructor(vararg ss: String) {
        s.addAll(ss)
    }

    // For OverRides
    constructor()

    private val obj = mutableListOf<TabObject>()
    private val s = mutableListOf<String>()

    open fun getAsList(): MutableList<String> {
        val list = mutableListOf<String>()
        obj.forEach { list.addAll(it.getAsList()) }
        list.addAll(s)
        return list
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[")
        getAsList().forEach { builder.append("$it,") }
        builder.append("]")
        return builder.toString()
    }

    open fun isMatch(s:String):Boolean{
        return getAsList().contains(s)
    }
}

class TabPart {
    companion object {
        val selectors = TabObject("@a", "@r", "@s", "@e","@p")
        val playerSelector = PlayerSelector()
    }

    class PlayerSelector() : TabObject() {
        override fun getAsList(): MutableList<String> = Bukkit.getOnlinePlayers().map { it.displayName }.toMutableList()
    }

    class EmptySelector():TabObject(){
        override fun isMatch(s: String): Boolean {
            return true
        }
    }
}