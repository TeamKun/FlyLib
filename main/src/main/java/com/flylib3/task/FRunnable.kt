package com.flylib3.task

import com.flylib3.FlyLib
import org.bukkit.scheduler.BukkitRunnable
import java.lang.IllegalArgumentException

/**
 * Note:Purpose for Overriding,parameter:lambda can be null.
 * @param st is starter of this runnable,for starter its own, it will be null.
 */
open class FRunnable<I, O>(
    val flyLib: FlyLib,
    var lambda: (FRunnableContext.(I) -> O)?,
    val st: FRunnableStarter<*>?
) : BukkitRunnable() {
    /**
     * Function that executes the lambda body and return value of it.
     * also, This function must call "then" elements.
     * Note:FRunnableContext should be inited in this function to call "then" elements with it.
     */
    open fun run(input: I, beforeContext: FRunnableContext?): O? {
        val context = createContext()
        if (lambda != null) {
            val o = lambda!!(context, input)
            if (!context.isMarkedBreakThen) {
                processThen(o, context)
            }
            return o
        }
        return null
    }

    /**
     * Call All Relatives which is connected this node with "then" function
     */
    open fun processThen(o: O, context: FRunnableContext) {
        allRelatives.forEach {
            call(it, o, context)
        }
    }

    override fun run() {
        // Call The Starter
        getStarter().run()
    }

    private fun call(r: BukkitRunnable, o: O, context: FRunnableContext) {
        if (r is FRunnable<*, *>) {
            val typeSafeFR = highRelatives.firstOrNull { it == r }
                ?: throw IllegalStateException("Can't find FRunnable in high-layer relatives(If you see this,this is the bug of FlyLib3)")
            // High-Layer Call
            typeSafeFR.run(o, context)
        } else {
            // Low-Layer Call
            r.run()
        }
    }

    private var context: FRunnableContext? = null
    protected fun createContext(): FRunnableContext {
        return if (context != null) context!!
        else {
            context = FRunnableContext(flyLib, this)
            context!!
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

    @Deprecated(message = "Please Use then(fRunnable)")
    fun then(runnable: BukkitRunnable): FRunnable<I, O> {
        lowRelatives.add(runnable)
        allRelatives.add(runnable)
        return this
    }

    /**
     * high-layer (not just for calling,with passing value)
     */
    private val highRelatives = mutableListOf<FRunnable<O, *>>()
    fun <S> then(fRunnable: FRunnable<O, S>): FRunnable<O, S> {
        highRelatives.add(fRunnable)
        allRelatives.add(fRunnable)
        return fRunnable
    }

    fun <S> then(f: FRunnableContext.(O) -> S): FRunnable<O, S> {
        return this.then(FRunnable<O, S>(flyLib, f, getStarter()))
    }

    fun getStarter(): FRunnableStarter<*> {
        return st
            ?: if (this is FRunnableStarter<*>)
                this
            else throw IllegalStateException("In FRunnable($this)#getStarter,this is not starter and st is null.(If you see this,this is the bug of FlyLib3.)")
    }
}

class FRunnableStarter<O>(flyLib: FlyLib, l: FRunnableContext.(Unit) -> O) : FRunnable<Unit, O>(flyLib, l, null) {
    override fun run() {
        // Only Starter is allowed to pass null value to beforeContext
        this.run(Unit, null)
    }
}

abstract class FRunnableNode<I, O>(flyLib: FlyLib, starter: FRunnableStarter<*>?) :
    FRunnable<I, O>(flyLib, null, starter) {
    override fun run(input: I, beforeContext: FRunnableContext?): O {
        if (beforeContext == null) {
            throw IllegalArgumentException("in FRunnableNode($this)#run,beforeContext is null")
        } else {
            val pair = onRun(input, beforeContext)
            if (!pair.first.isMarkedBreakThen) {
                processThen(pair.second, pair.first)
            }
            return pair.second
        }
    }

    abstract fun onRun(input: I, beforeContext: FRunnableContext): Pair<FRunnableContext, O>
}

class FRunnableContext(val flyLib: FlyLib, val element: FRunnable<*, *>) {
    var isMarkedBreakThen = false
        private set

    /**
     * Mark as not to call next then statement
     */
    fun breakThen(breakThen: Boolean = true) {
        isMarkedBreakThen = breakThen
    }
}