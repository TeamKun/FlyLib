package com.flylib3.example

import com.flylib3.FlyLibPlugin
import com.flylib3.test.aCommand
import com.flylib3.test.executeCommand
import com.flylib3.util.command

class ExamplePlugin : FlyLibPlugin() {
    override fun onWait() {
        // Loading is Completed
        // Maybe Command Registers will be here
        super.onWait() // THIS IS NEEDED

        command("testCommand") {
            part<String>("String", "String2") {
                part<Int>(1, 2, 3) {
                    terminal {
                        usage("This is Usage")
                        permission { commandSender -> commandSender.isOp }
                        execute(::executeCommand)
                    }
                }

                part<String>("A") {
                    terminal {
                        execute(::aCommand)
                    }
                }
            }
        }
    }

    override fun onPrepare() {
        // Prepare here for action
        // Maybe Command Executors call this
        super.onPrepare() // THIS IS NEEDED
    }

    override fun onAction() {
        // Action Started
        // Maybe Prepare Phase call this
        super.onAction() // THIS IS NEEDED
    }
}