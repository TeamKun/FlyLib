package com.flylib3.task

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import org.bukkit.event.Event
import org.bukkit.scheduler.BukkitRunnable
import kotlin.reflect.KClass

class FTaskManager(override val flyLib: FlyLib) : FlyLibComponent {
    fun <T> everyTick(f: FRunnableContext.(Unit) -> T): FRunnableStarter<Unit, T> {
        val starter = runnableStarter(f)
        everyTick(starter)
        return starter
    }

    fun everyTick(fRunnable: FRunnableStarter<*, *>) {
        timer(fRunnable)
    }

    fun <T> nextTick(f: FRunnableContext.(Unit) -> T): FRunnableStarter<Unit, T> {
        val starter = runnableStarter(f)
        nextTick(starter)
        return starter
    }

    fun nextTick(fRunnable: FRunnableStarter<*, *>) {
        run(fRunnable)
    }

    fun <T> later(f: FRunnableContext.(Unit) -> T, delay: Long): FRunnableStarter<Unit, T> {
        val starter = runnableStarter(f)
        later(starter, delay)
        return starter
    }

    fun later(fRunnable: FRunnableStarter<*, *>, delay: Long) {
        taskLater(fRunnable, delay)
    }

    fun <T> task(f: FRunnableContext.(Unit) -> T): FRunnableStarter<Unit, T> {
        return runnableStarter(f)
    }

    fun <T : Event, R> event(event: KClass<T>, f: FRunnableContext.(T) -> R): EventRunnableStarter<T, R> {
        return eventStarter(event, f)
    }

    inline fun <reified T : Event, R> event(noinline f: FRunnableContext.(T) -> R): EventRunnableStarter<T, R> {
        return event(T::class, f)
    }

    fun <T> runnableStarter(f: FRunnableContext.(Unit) -> T) = FRunnableUnitStarter<T>(flyLib, f)
    private fun <T : Event, R> eventStarter(event: KClass<T>, f: FRunnableContext.(T) -> R) =
        EventRunnableStarter<T, R>(event, flyLib, f)

    private fun timer(runnable: BukkitRunnable, delay: Long = 0, period: Long = 1) {
        runnable.runTaskTimer(flyLib.plugin, delay, period)
    }

    private fun taskLater(runnable: BukkitRunnable, delay: Long) {
        runnable.runTaskLater(flyLib.plugin, delay)
    }

    private fun run(runnable: BukkitRunnable) {
        runnable.runTask(flyLib.plugin)
    }
}