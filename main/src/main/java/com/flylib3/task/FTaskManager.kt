package com.flylib3.task

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import org.bukkit.scheduler.BukkitRunnable

class FTaskManager(override val flyLib: FlyLib) : FlyLibComponent {
    fun <T> everyTick(f: FlyLib.(Unit) -> T): FRunnableStarter<T> {
        val starter = runnableStarter(f)
        everyTick(starter)
        return starter
    }

    fun everyTick(fRunnable: FRunnableStarter<*>) {
        timer(fRunnable)
    }

    fun <T> nextTick(f: FlyLib.(Unit) -> T): FRunnableStarter<T> {
        val starter = runnableStarter(f)
        nextTick(starter)
        return starter
    }

    fun nextTick(fRunnable: FRunnableStarter<*>) {
        run(fRunnable)
    }

    fun <T> later(f: FlyLib.(Unit) -> T, delay: Long): FRunnableStarter<T> {
        val starter = runnableStarter(f)
        later(starter, delay)
        return starter
    }

    fun later(fRunnable: FRunnableStarter<*>, delay: Long) {
        taskLater(fRunnable, delay)
    }

    fun <T> runnableStarter(f: FlyLib.(Unit) -> T) = FRunnableStarter<T>(flyLib, f)
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