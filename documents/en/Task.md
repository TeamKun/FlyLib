# Task Feature

FlyLib3 has strong task feature.<br/>
This is the reference of this feature.<br/>

# Example

Examples of this feature is found
on [TaskTest.kt](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/test/TaskTest.kt) <br/>

# Reference

Task Feature has easy way to manage tasks which across many ticks.<br/>

# TaskStarter

TaskStarter is the first element of a chain of tasks.<br/>
There are four TaskStarters.<br/>

1) everyTick
2) nextTick
3) later
4) task

## EveryTick

```kotlin
fun <T> FlyLibPlugin.everyTick(l: FRunnableContext.(Unit) -> T)
```

everyTick element runs its chain every tick.<br/>

## NextTick

```kotlin
fun <T> FlyLibPlugin.nextTick(l: FRunnableContext.(Unit) -> T)
```

nextTick element runs its chain next tick.<br/>

## Later

```kotlin
fun <T> FlyLibPlugin.later(l: FRunnableContext.(Unit) -> T, delay: Long)
```

later element run its chain later tick.<br/>

## Task

```kotlin
fun <T> FlyLibPlugin.task(l: FRunnableContext.(Unit) -> T)
```

Task element **_will not run_** its chain immediately.<br/>
For Run this element,call ``run()``

```kotlin
val task = task {
    // Something
}.then {
    // Then Body
}

task.run()
```

or

```kotlin
task {
    // Something
}.then {
    // Then Body
}.run()
```

# TaskNode

TaskNode process value/data from before node/starter and pass processed value to next node.<br/>

There are two types of TaskNode.<br/>

1) then
2) wait

## Then

```kotlin
fun <S> then(f: FRunnableContext.(O) -> S)
```

```kotlin
everyTick {
    // Something
}.then {
    // Then Body
}
```

Then function connect two elements.<br/>
Then element waits before task and receive its return value,executes its body.<br/>

## Wait

```kotlin
fun <I> FRunnable<*, I>.wait(ticks: Long)
```

```kotlin
nextTick {
    // Something
}
    .wait(20 * 3)
    .then {
        // Then Body
    }
```

Wait function connect two elements with delay.<br/>
