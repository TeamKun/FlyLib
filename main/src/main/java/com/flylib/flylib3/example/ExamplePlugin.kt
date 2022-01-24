package com.flylib.flylib3.example

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.FCommandEvent
import com.flylib.flylib3.util.*
import org.bukkit.event.player.PlayerMoveEvent

class ExamplePlugin : FlyLibPlugin() {
    init {
        ready {
            // Most time,flylib is already loaded at this point.
            info("FlyLib is ready")
        }
    }

    override fun enable() {
        command("testCommand") {
            part<String>("String", "String2") {
                part<Int>(1, 2, 3) {
                    terminal {
                        usage("This is Usage")
                        permission { commandSender, _, _, _ -> commandSender.isOp }
                        execute(this@ExamplePlugin::executeCommand)
                    }
                }

                part<String>("A") {
                    terminal {
                        execute(this@ExamplePlugin::aCommand)
                    }
                }
            }
        }


        task {
            info("Start Up!")
        }.run()

        event<PlayerMoveEvent, Unit> {
            it.player.sendMessage("Player Move!")
        }
    }

    override fun disable() {
    }

    fun executeCommand(event: FCommandEvent, str: String, int: Int): Boolean {
        info("Execute Part")
        event.commandSender.sendMessage("str:$str,int:$int")
        later(20 * 5) {
            event.commandSender.sendMessage("After 5 seconds")
        }
        return true
    }

    fun aCommand(event: FCommandEvent, str: String, str2: String): Boolean {
        event.commandSender.sendMessage("A!")
        return true
    }
}