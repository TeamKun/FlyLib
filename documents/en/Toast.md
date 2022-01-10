# Toast Feature

FlyLib3 has Toast Feature.<br/>

# Usage

For deeply,see
at[ToastGenerator](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/toast/ToastGenerator.kt) <br/>

```kotlin
class ToastGenerator(
    val icon: Material,
    val title: String,
    val description: String = "",
    val background: String = "minecraft:textures/gui/advancements/backgrounds/adventure.png",
    val frame: FrameType = FrameType.Goal,
    val uuid: UUID = UUID.randomUUID()
)

class Toast(val generator: ToastGenerator, override val flyLib: FlyLib)
```

For Show Toast,<br/>

```kotlin
Toast.show(player)
```