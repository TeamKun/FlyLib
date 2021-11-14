package com.flylib3.example

import com.flylib3.FlyLibPlugin
import com.flylib3.command.Command
import com.flylib3.command.CommandBuilder

class ExamplePlugin : FlyLibPlugin() {
    override fun onWait() {
        // Loading is Completed
        // Maybe Command Registers will be here
        super.onWait() // THIS IS NEEDED

        val command = CommandBuilder(flylib)
            .name("test")
            .execute { sender, command, label, args ->
                sender.sendMessage("Hi! From FlyLib3 CommandSystem!")
                return@execute true
            }
            .tabComplete { sender, command, alias, args ->
                return@tabComplete mutableListOf("TestCompleter")
            }
            .permission { it.isOp }
            .permissionMessage { it.sendMessage("You don't have enough Permission") }
            .usage("/<command>")
            .buildForce()

        flylib.command.register(command)
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