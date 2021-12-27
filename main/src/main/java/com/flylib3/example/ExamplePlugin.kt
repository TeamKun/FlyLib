package com.flylib3.example

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.util.command
import java.time.LocalDate

class ExamplePlugin : FlyLibPlugin() {
    override fun onEnable() {
        command("testCommand") {
            part<String>("String", "String2") {
                part<Int>(1, 2, 3) {
                    terminal {
                        usage("This is Usage")
                        permission { commandSender, _, _, _ -> commandSender.isOp }
                        execute(ExamplePlugin::executeCommand)
                    }
                }

                part<String>("A") {
                    terminal {
                        execute(ExamplePlugin::aCommand)
                    }
                }
            }
        }
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
}