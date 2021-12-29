package com.flylib3.util

import com.flylib3.FlyLib
import com.flylib3.task.FRunnable
import com.flylib3.task.FRunnableContext
import com.flylib3.task.FRunnableNode
import com.flylib3.task.FRunnableStarter

fun <I> FRunnable<*, I>.wait(ticks: Long): WaitFR<I> {
    val wait = WaitFR<I>(ticks, flyLib, getStarter())
    then(wait)
    return wait
}

class WaitFR<I>(val ticks: Long, flyLib: FlyLib, starter: FRunnableStarter<*>?) : FRunnableNode<I, I>(flyLib, starter) {
    override fun onRun(input: I, beforeContext: FRunnableContext): Pair<FRunnableContext, I> {
        val context = createContext()
        context.breakThen()
        flyLib.task.later({ processThen(input,context) }, ticks)
        return Pair(context, input)
    }
}