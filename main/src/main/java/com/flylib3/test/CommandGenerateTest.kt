package com.flylib3.test

import com.flylib3.FlyLib
import com.flylib3.event.ex.FCommandEvent

fun main() {

}

fun executeCommand(event: FCommandEvent, str: String, int: Int): Boolean {
    println("Execute Part")
    event.commandSender.sendMessage("str:$str,int:$int")
    return true
}

fun aCommand(event: FCommandEvent, str: String, str2: String): Boolean {
    event.commandSender.sendMessage("A!")
    return true
}