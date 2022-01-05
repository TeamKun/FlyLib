package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.event.ex.FCommandEvent
import com.flylib3.util.*

class TaskTest : FlyLibPlugin() {
    override fun enable() {
        command("testCommand") {
            part("run") {
                terminal {
                    execute(this@TaskTest::call)
                }
            }
        }

        task {
            println("AAA")
        }.then {
            println("BBB")
        }.run()

        task {
            println("CCC")
        }.filter { _, _ ->
            return@filter false
        }.then {
            println("This will not be called.")
        }.run()
    }

    override fun disable() {
    }

    fun call(e: FCommandEvent, str: String): Boolean {
        task {
            val players = flyLib.plugin.server.onlinePlayers

            players.forEach {
                it.sendMessage("Now,Shuffling")
            }

            // Process Something
            return@task players.shuffled()
        }
            .wait(20 * 2)
            .then {
                it.forEachIndexed { index, player ->
                    player.sendMessage("Sending With Delay!")
                    player.sendMessage("Your index is $index")
                }
            }.run()
        return true
    }
}