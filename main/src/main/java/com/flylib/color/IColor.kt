package com.flylib.color

import org.bukkit.Color
import org.bukkit.DyeColor

interface IColor {
    fun getDyeColor(): DyeColor?
    fun setColor(color: IColor)
    fun getColor(): Color
}

class ColorHelper() {

}

enum class DefaultColors(private val dye: DyeColor) : IColor {
    WHITE(DyeColor.WHITE),
    ORANGE(DyeColor.ORANGE),
    MAGENTA(DyeColor.MAGENTA),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    PINK(DyeColor.PINK),
    GRAY(DyeColor.GRAY),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    CYAN(DyeColor.CYAN),
    PURPLE(DyeColor.PURPLE),
    BLUE(DyeColor.BLUE),
    BROWN(DyeColor.BROWN),
    GREEN(DyeColor.GREEN),
    RED(DyeColor.RED),
    BLACK(DyeColor.BLACK);

    override fun getDyeColor(): DyeColor = dye
    override fun setColor(color: IColor) {}
    override fun getColor(): Color = dye.color

}