package com.flylib3.task

import com.flylib3.FlyLib
import org.bukkit.scheduler.BukkitRunnable

open class FRunnable<I, O>(val flyLib: FlyLib, var lambda: FlyLib.(I) -> O) : BukkitRunnable() {
    fun run(input: I): O {
        val o = lambda(flyLib, input)
        // Process "Then"
        allRelatives.forEach {
            call(it, o)
        }

        return o
    }

    override fun run() {
        // MayBe Do Nothing
    }

    private fun call(r: BukkitRunnable, o: O) {
        if (r is FRunnable<*, *>) {
            val typeSafeFR = highRelatives.firstOrNull { it == r }
                ?: throw IllegalStateException("Can't find FRunnable in high-layer relatives(If you see this,this is the bug of FlyLib3)")
            // High-Layer Call
            typeSafeFR.run(o)
        } else {
            // Low-Layer Call
            r.run()
        }
    }

    /**
     * All Relatives down cast into BukkitRunnable
     */
    private val allRelatives = mutableListOf<BukkitRunnable>()

    /**
     * low-layer (just for calling)
     */
    private val lowRelatives = mutableListOf<BukkitRunnable>()
    fun then(runnable: BukkitRunnable): FRunnable<I, O> {
        lowRelatives.add(runnable)
        allRelatives.add(runnable)
        return this
    }

    /**
     * high-layer (not just for calling,with passing value)
     */
    private val highRelatives = mutableListOf<FRunnable<O, *>>()
    fun then(fRunnable: FRunnable<O, *>): FRunnable<I, O> {
        highRelatives.add(fRunnable)
        allRelatives.add(fRunnable)
        return this
    }

    fun <S> then(f: FlyLib.(O) -> S): FRunnable<I, O> {
        return this.then(FRunnable<O, S>(flyLib, f))
    }
}

class FRunnableStarter<O>(flyLib: FlyLib, val l: FlyLib.(Unit) -> O) : FRunnable<Unit, O>(flyLib, l) {
    override fun run() {
        this.run(Unit)
    }
}