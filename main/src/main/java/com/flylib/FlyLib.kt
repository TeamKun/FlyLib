package com.flylib

import com.flylib.event.MainListener
import com.flylib.gui.ChestGUI
import com.flylib.item.EasyItemBuilder
import com.flylib.spawnegg.SpawnEggUtil
import com.flylib.state.State
import com.flylib.util.NaturalNumber
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FlyLib(val plugin: JavaPlugin) {
    companion object{
        var instance:FlyLib? = null
        fun isExist() = instance!=null
        fun get(): FlyLib? {
            if(isExist()) return instance
            throw FlyLibNotReadyException()
        }
    }

    val state = State()
    val listener = MainListener(this)
    val itemBuilder = EasyItemBuilder()
    val spawnEggUtil = SpawnEggUtil()

    fun generateGUI(p: Player, col: NaturalNumber, name: String): ChestGUI {
        return ChestGUI(p, col, name)
    }
}

class FlyLibNotReadyException:Exception("FlyLib is not ready")