package com.github.bun133.flylib2.utils

import net.kyori.adventure.text.TextComponent
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign

class SignUtils {
    companion object{
        fun gen(loc:Location): Sign? {
            loc.block.type = Material.OAK_SIGN
            val meta = loc.block.blockData
            return if(meta is Sign){
                meta
            }else{
                null
            }
        }

        @JvmName("genFromString")
        fun gen(loc:Location, lines:List<String>): Sign? {
            return gen(loc,ComponentUtils.fromText(lines))
        }

        @JvmName("genFromComponent")
        fun gen(loc:Location,comp:List<TextComponent>): Sign? {
            val sign = gen(loc) ?: return null
            sign.set(comp)
            return sign
        }
    }
}

fun Sign.set(comp:List<TextComponent>){
    comp.forEachIndexed { index, textComponent -> line(index,textComponent) }
}