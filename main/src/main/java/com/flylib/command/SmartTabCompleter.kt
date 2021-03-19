package com.flylib.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.lang.IndexOutOfBoundsException

class TabPart {
    companion object {
        val selectors = TabObject("@a", "@r", "@s", "@e")
        val playerSelector = PlayerSelector()
    }

    class PlayerSelector() : TabObject() {
        override fun getAsList(): MutableList<String> = Bukkit.getOnlinePlayers().map { it.displayName }.toMutableList()
    }
}

class SmartTabCompleter(vararg c: TabChain) : TabCompleter {
    private val chains = listOf(*c)
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
//        println("input:${args.contentToString()}")
        val strings = args.toMutableList().map { it as String }.toTypedArray()
        val list = mutableListOf<String>()
        chains
            .filter { it.isExist(strings) }
            .forEach { chain ->
//                println("Chain:${chain.toString()}")
                chain.getMatched(strings)!!.getAsList()
                    // ADDED
                    .filter { it.startsWith(strings[strings.lastIndex]) }
                    .forEach { list.add(it) }
            }
//        println("Result:${list.toString()}")
        if (list.isEmpty()) list.add("")
        return list
    }
}

/**
 * The one way of command,
 * like,(TabObject("kill","heal","give"),TabObject("@a","@r","@s","@e"))
 */
class TabChain(vararg tab: TabObject) {
    private val tabObjects = listOf(*tab)

    fun getMatched(args: Array<String>): TabObject? {
//        println("tabChain:${toString()}")
//        println("tabObjects.lastIndex:${tabObjects.lastIndex}")
//        println("args.lastIndex:${args.lastIndex}")

        if(args.size - 1 > tabObjects.size){
//            println("Skipped:Not Enough Length")
            return null
        }

        if (args.isEmpty()) {
            return tabObjects[0]
        }

        var isMatched = true

        tabObjects.forEachIndexed { index, tabObject ->
            if (index >= args.lastIndex) {
//                println("Skipped:Index Out")
            } else {
                if (!tabObject.getAsList().contains(args[index])) {
                    isMatched = false
                }
            }
        }

        if (!isMatched) {
//            println("Not Matched!")
            return null
        }else{
            return try{
                tabObjects[args.lastIndex]
            }catch (e:IndexOutOfBoundsException){
                null
            }
        }


        /*
        if (args.size - 1 > tabObjects.size) {
            println("Not Enough Length")
            return null
        }

        var isMatched = true

        args.forEachIndexed { index, s ->
            if (s == "" && index == args.lastIndex) {
                println("Last And Return")
            } else {
                println("index:${index} s:${s}")
                val b = tabObjects[index].getAsList().any {
                    it.startsWith(s)
                }
                if (!b) {
                    isMatched = false
                }
            }
        }

        if (args[args.lastIndex] == "") {
            println("Last is Empty")
            if (tabObjects.lastIndex >= args.lastIndex) {
                return tabObjects[args.lastIndex]
            }
        }

        if (isMatched) {
            return try {
                tabObjects[args.lastIndex]
            }catch (e:IndexOutOfBoundsException){
                null
            }
        } else {
            println("Not Matched")
            return null
        }*/

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

/**
 * Part Of Tab Chain,
 * like ("@a","@r","@s","@e")
 * this will provide the player selector.
 */
open class TabObject {
    private val strings: MutableList<String> = mutableListOf()
    private val objects: MutableList<TabObject> = mutableListOf()

    constructor(vararg args: String) {
        strings.addAll(arrayOf(*args))
    }

    constructor(vararg objs: TabObject) {
        objects.addAll(arrayOf(*objs))
    }

    // Only For Override
    constructor()

    open fun getAsList(): MutableList<String> {
        val list = mutableListOf<String>()
        list.addAll(strings)
        objects.map { it.getAsList() }.forEach { obj -> obj.forEach { list.add(it) } }
        return list
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[")
        getAsList().forEach { builder.append("$it,") }
        builder.append("]")
        return builder.toString()
    }
}