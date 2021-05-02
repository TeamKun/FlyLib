package com.github.bun133.flylib2.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent

class ComponentUtils {
    companion object{
        fun toText(comp:Component): String {
            return if(comp is TextComponent){
                comp.content()
            }else{
                comp.toString()
            }
        }

        fun fromText(s:String): TextComponent {
            return Component.text(s)
        }

        fun fromText(s:List<String>): List<TextComponent> {
            return s.map { fromText(it) }
        }
    }
}