package com.flylib3.test

import com.flylib3.FlyLibPlugin
import com.flylib3.util.everyTick

class TaskTest : FlyLibPlugin() {
    override fun onEnable() {
        everyTick {
            return@everyTick plugin.server.onlinePlayers
        }.then {
            return@then ""
        }.then {
            it
        }

        everyTick {
            return@everyTick plugin.server.onlinePlayers
        }
    }
}