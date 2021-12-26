# Command Feature

FlyLib3 has strong command feature.<br/>
This is the reference of this feature.<br/>

# Example

Examples of this feature is found
on [CommandGenerateTest.kt](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/test/CommandGenerateTest.kt)

# Reference

Command Feature has easy way to create/build command.<br/>

```kotlin
command("testCommand") {
    part<String>("String", "String2") {
        part<Int>(1, 2, 3) {
            terminal {
                usage("This is Usage")
                permission { commandSender, _, _, _ -> commandSender.isOp }
                execute(functionHere)
            }
        }

        part<LocalDate>(
            LocalDate::class.createType(),
            { _, _, _, _ -> listOf(LocalDate.now()) },
            { LocalDate.parse(it) }
        ) {
            terminal {
                execute(functionHere)
            }
        }
    }
}
```

CommandBuilder has mainly 3 types of functions.<br/>

## Starter

Starter provides the start point of Command Building.<br/>

```kotlin
fun FlyLibPlugin.command(commandName: String, alias: List<String> = listOf(), lambda: FCommandBuilder.() -> Unit)
```

```commandName``` is the name of command to build.<br/>
```alias``` is the alias of that command(optional).<br/>
```lambda``` is building body(see below).<br/>

## Part

Part is the part of command or One Argument.<br/>

```kotlin
fun <T : Any> part(vararg t: T, lambda: FCommandBuilderPart<T>.() -> Unit)
fun <T : Any> part(
    type: KType,
    lazyValues: (
        CommandSender,
        Command,
        String,
        Array<out String>
    ) -> List<T>,
    lazyParser: (String) -> T?,
    lambda: FCommandBuilderPart<T>.() -> Unit
)
```

### Upper Function

```kotlin
fun <T : Any> part(vararg t: T, lambda: FCommandBuilderPart<T>.() -> Unit)
```

```t``` is the suggested values as argument.<br/>
e.g. if you want to make command,which suggests "a" and "b" in first argument.<br/>
The code will be:<br/>

```kotlin
command("commandname") {
    part<String>("a", "b") {
        // and more
    }
}
```

### Lower Function

```kotlin
fun <T : Any> part(
    type: KType,
    lazyValues: (
        CommandSender,
        Command,
        String,
        Array<out String>
    ) -> List<T>,
    lazyParser: (String) -> T?,
    lambda: FCommandBuilderPart<T>.() -> Unit
)
```

This function is for a situation which need to update suggest realtime,or for needs to use auto-convert-type
system.<br/>
```type``` is the KType of ```T``` (Note:You can get KType by

```kotlin
Hoge::class.createType()
```

)
```lazy_values``` is lambda that returns suggest realtime.(Note:```Array<out String>``` is argument before this
argument.)<br/>
```lazy_parser``` is lambda that parse values from string.If it is not able,return null.<br/>

## Terminal

Terminal is the end part of command builder.<br/>
FlyLib3 recognize the terminal function as the end of the command.<br/>

```kotlin
fun terminal(lambda: FCommandBuilderPath.() -> Unit)
```

There is no argument to write,but in ```lambda``` you can write various settings of command.<br/>

### Terminal Settings

```kotlin
command("commandname") {
    path<String>("a", "b") {
        terminal {
            usage("Usage String")
            permission { CommandSender, Command, String, Array<out String> -> CommandSender.isOp }
            execute(::func)
        }
    }
}

//  ...
//  ...
//  ...

fun func(event: FCommandEvent, arg1: String) {
//  Execute Body    
}
```

```usage``` is function which append usage data to the command.(maybe WIP)<br/>
```permission``` is function which decide who can execute this command.<br/>
```execute``` is function which set real executor.<br/>

Note:The parameters of executor function must follow these below rules.<br/>

1) The first parameter is FCommandEvent.
2) The rest parameters are match with the types of parts which construct this way.