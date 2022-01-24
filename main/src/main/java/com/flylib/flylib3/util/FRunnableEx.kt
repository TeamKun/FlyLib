package com.flylib.flylib3.util

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.task.FRunnable
import com.flylib.flylib3.task.FRunnableContext
import com.flylib.flylib3.task.FRunnableNode
import com.flylib.flylib3.task.FRunnableStarter

/**
 * Stop the chain,wait ticks,then recall chains after this element.
 */
fun <I> FRunnable<*, I>.wait(ticks: Long): WaitFR<I> {
    val wait = WaitFR<I>(ticks, flyLib, getStarter())
    then(wait)
    return wait
}

class WaitFR<I>(val ticks: Long, flyLib: FlyLib, starter: FRunnableStarter<*, *>?) :
    FRunnableNode<I, I>(flyLib, starter) {
    override fun onRun(input: I, beforeContext: FRunnableContext): Pair<FRunnableContext, I> {
        val context = createContext()
        context.breakThen()
        flyLib.task.later({ processThen(input, context) }, ticks)
        return Pair(context, input)
    }
}

/**
 * @param f if returns true,this chain continues to call. if returns false,chains after this element winn not be called.
 */
fun <I> FRunnable<*, I>.filter(f: (I, FRunnableContext) -> Boolean): FilterFR<I> {
    val filter = FilterFR<I>(flyLib, getStarter(), f)
    then(filter)
    return filter
}

class FilterFR<I>(flyLib: FlyLib, starter: FRunnableStarter<*, *>?, var f: (I, FRunnableContext) -> Boolean) :
    FRunnableNode<I, I>(flyLib, starter) {
    override fun onRun(input: I, beforeContext: FRunnableContext): Pair<FRunnableContext, I> {
        val context = createContext()
        if (!f(input, beforeContext)) {
            context.breakThen()
        }
        return Pair(context, input)
    }
}