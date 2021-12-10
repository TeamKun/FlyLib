package com.flylib3.test

import com.flylib3.util.Tree
import com.flylib3.util.TreeEntry

fun main() {
    val root = Tree<String>()
    root.append(
        "Value1",
        "Value2",
        "Value3"
    )

    root.append(
        TreeEntry<String>()
            .append("Entry1/Value1", "Entry1/Value2", "Entry1/Value3"),
        TreeEntry<String>()
            .append(
                TreeEntry<String>()
                    .append("Entry2/Entry1/Value1")
            )
    )

    println("===Show All===")
    root.all {
        println(it)
    }
    println("===Filter===")
    root.findAll { it.isValue() && it.values().size == 3 }.forEach {
        println(it)
    }
}