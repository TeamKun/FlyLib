package com.github.bun133.flylib2.attribute

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class AttributeUtil {
    companion object {
        fun setAttribute(p: Player, attribute: Attribute, value: Double): Boolean {
            try {
                p.getAttribute(attribute)!!.baseValue = value
            } catch (e: NullPointerException) {
                return false
            }
            return true
        }

        fun resetAttribute(p: Player, attribute: Attribute): Boolean {
            try {
                val attr = p.getAttribute(attribute)!!
                attr.baseValue = attr.defaultValue
            } catch (e: NullPointerException) {
                return false
            }
            return true
        }
    }
}