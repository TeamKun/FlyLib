package com.flylib.flylib3.test

import com.flylib.flylib3.FlyLibPlugin
import com.flylib.flylib3.event.ex.ExternalEvent
import com.flylib.flylib3.util.info
import com.flylib.flylib3.util.task
import com.flylib.flylib3.util.wait
import org.bukkit.event.EventHandler

class EventTest : FlyLibPlugin() {
    override fun enable() {
        task { flyLib.log.info { "${it}Task Start" } }
            .wait(20 * 5)
            .then { flyLib.log.info { "${it}Calling Event Now..." } }
            .then { callEvent(TestEvent(server.currentTick)) }
            .run()
    }

    override fun disable() {
    }

    @EventHandler
    fun testEvent(event: TestEvent) {
        info { "${it}Test Event Function Called" }
    }
}

class TestEvent(val currentTick: Int) : ExternalEvent()