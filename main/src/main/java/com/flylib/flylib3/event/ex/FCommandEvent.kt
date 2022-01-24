package com.flylib.flylib3.event.ex

import com.flylib.flylib3.command.FCommandBuilderPath
import org.bukkit.command.CommandSender

class FCommandEvent(val commandSender: CommandSender, val path: FCommandBuilderPath) : ExternalEvent() {
}