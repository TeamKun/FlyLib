# Event System

FlyLib3 has simple event system that allows to define/execute custom event.<br/>

# Reference

To define custom
event,Override [ExternalEvent](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/event/ex/ExternalEvent.kt) <br/>
To Execute that
event,Call [FlyLibPlugin#callEvent](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/FlyLibPlugin.kt) <br/>
(or just call PluginManager(bukkit)#callEvent)<br/>

To receive that event,just write function with @EventListener annotation.<br/>
*Note:No need to registerEvents,FlyLib3 already done that.*<br/>

# FlyLibDefaultExEvents

FlyLib has default external event.<br/>
For Details,please see
at [FlyLibDefaultExEvents](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/event/ex/flylib/FlyLibDefaultExEvents.kt) <br/>

| **Event**               | **Description**                                         |
|-------------------------|---------------------------------------------------------|
| `PlayerLeftClickEvent`  | fires When The player left clicks.                      |
| `PlayerRightClickEvent` | fires When The player right clicks.(Please see at note) |

# Example

See at [TaskTest](https://github.com/TeamKun/FlyLib/blob/flylib-3/main/src/main/java/com/flylib3/test/TaskTest.kt) <br/>