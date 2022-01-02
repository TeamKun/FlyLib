package com.flylib3.gui;

import com.flylib3.FlyLibComponent
import org.bukkit.entity.Player

/**
 * Base class of FlyLib3 GUI Component
 * @param T T is the type that express gui component at a pos.
 * @param X The type of x-axis
 * @param Y The type of y-axis
 * @param P The type of Pos
 * e.g. ChestGUI -> ItemStack?
 */
interface FGUIComponent<T, X, Y, P : Pos<X, Y>> : FlyLibComponent {
    /**
     * @return width of this gui
     */
    fun width(): Int

    /**
     * @return height of this gui
     */
    fun height(): Int

    /**
     * open gui to player
     * @param forceKeepOpen true to keep player open this gui
     */
    fun open(p: Player, forceKeepOpen: Boolean = false)

    /**
     * close gui to player
     */
    fun close(p: Player)

    /**
     * @return gui entry at (x,y)
     * @note index start from 1
     */
    operator fun get(x: X, y: Y): T
    operator fun get(pos: P): T

    /**
     * @return all entry in this gui
     */
    fun getAll(): List<T>

    /**
     * @return all acceptable pos in this gui
     */
    fun getAllPos(): List<P>

    /**
     * set gui entry at (x,y)
     * @note index start from 1
     */
    operator fun set(x: X, y: Y, t: T)
    operator fun set(pos: P, t: T)

    /**
     * @return all player that is seeing this gui
     */
    fun getAllSeer(): List<Player>
}