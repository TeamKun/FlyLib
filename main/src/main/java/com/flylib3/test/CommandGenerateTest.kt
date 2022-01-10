package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.util.command
import java.time.LocalDate
import kotlin.reflect.full.createType

/**
 *  PASSED AT 17:26 2021/12/22
 */
class CommandGenerateTestPlugin() : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part<String>("String", "String2") {
                part<Int>(1, 2, 3) {
                    terminal {
                        usage("This is Usage")
                        permission { commandSender, _, _, _ -> commandSender.isOp }
                        execute(this@CommandGenerateTestPlugin::executeCommand)
                    }
                }

                part<LocalDate>(
                    LocalDate::class.createType(),
                    { _, _, _, _ -> listOf(LocalDate.now()) },
                    { LocalDate.parse(it) }
                ) {
                    terminal {
                        execute(this@CommandGenerateTestPlugin::aCommand)
                    }
                }
            }
        }
    }

    override fun disable() {
    }

    fun executeCommand(event: FCommandEvent, str: String, int: Int): Boolean {
        println("Execute Part")
        event.commandSender.sendMessage("str:$str,int:$int")
        return true
    }

    fun aCommand(event: FCommandEvent, str: String, date: LocalDate): Boolean {
        event.commandSender.sendMessage("A!Today is:$date")
        return true
    }
}