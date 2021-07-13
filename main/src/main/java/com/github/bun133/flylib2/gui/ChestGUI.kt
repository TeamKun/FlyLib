package com.github.bun133.flylib2.gui

import com.github.bun133.flylib2.utils.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class ChestGUI(private val owning: Player, name: String = "Chest", val cols: Int = 4, private val plugin: JavaPlugin) :
    Listener {
    @Suppress("MemberVisibilityCanBePrivate")
    val chest: Inventory
    internal var list = SizedFlatList<GUIEntry?>(9, cols)

    init {
        if (cols <= 0 || cols >= 7) throw IllegalArgumentException("Col Must Be 1 <= col <= 6")
        chest = Bukkit.createInventory(owning, cols * 9, ComponentUtils.fromText(name))
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onDrop(e: InventoryMoveItemEvent) {
        if (e.destination == chest) {
            e.isCancelled = true
        }
    }

    fun set(x: Int, y: Int, entry: GUIEntry?): ChestGUI {
        list.outCheck(x, y)
        list.set(x, y, entry)
        sync()
        return this
    }

    fun get(x: Int, y: Int): GUIEntry? {
        list.outCheck(x, y)
        return list.get(x, y)?.t
    }

    fun clear() {
        list = SizedFlatList(9, cols)
        sync()
    }

    fun open() {
        open(owning)
    }

    fun open(p: Player) {
        p.closeInventory()
        sync()
        p.openInventory(chest)
    }

    fun gen(stack: ItemStack): GUIEntry = GUIEntry(stack, plugin)
    fun gen(x: Int, y: Int, stack: ItemStack): GUIEntry {
        val e = gen(stack)
        set(x, y, e)
        return e
    }

    private fun sync() {
        for (x in 1..list.width) {
            for (y in 1..list.height) {
                val now = chest.getItem(x - 1 + (y - 1) * 9)
                val after = list.get(x, y)
                if (after == null) {
                    if (isDiffer(now, null)) {
                        chest.setItem(x - 1 + (y - 1) * 9, null)
                    }
                } else {
                    if (isDiffer(now, after.t)) {
                        chest.setItem(x - 1 + (y - 1) * 9, after.t?.itemStack)
                    }
                }
            }
        }
    }

    private fun isDiffer(stack: ItemStack?, entry: GUIEntry?): Boolean {
        if (stack == null && entry == null) return false
        if ((stack != null && entry == null) || (stack == null && entry != null)) return true
        if (stack != null && entry != null) {
            return stack.isSimilar(entry.itemStack)
        }
        println("[isDiffer]:ERROR")
        return true
    }
}

class GUIEntry(val itemStack: ItemStack, plugin: JavaPlugin) : Listener {
    private val marker = ItemMarker(itemStack, plugin)

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    val register = Registry<InventoryClickEvent>()

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.currentItem != null) {
            if (e.currentItem!!.type == itemStack.type) {
                if (marker.isMatched(e.currentItem!!)) {
                    register.execute(e)
                    e.isCancelled = true
                }
            }
        }
    }
}

class PagedChestGUI(
    private val owning: Player,
    val plugin: JavaPlugin,
    private val name: String = "Chest",
    private val cols: Int = 4,
    private val ui: PageUIDrawer = DefaultPageUI()
) {
    companion object {
        /**
         * たぶんとても効率が悪い
         */
        fun addAll(gui: PagedChestGUI, items: List<GUIEntry>) {
//            items.forEach {
//                val pos = getAddPoint(gui)
//                if (pos == null) {
//                    gui.newPage().chest.set(1, 1, it)
//                } else {
//                    pos.first.chest.set(pos.second.first, pos.second.second, it)
//                }
//            }

            // 強引調整案
//            gui.list.removeAll { p -> p.chest.list.any { it.t == null || it.t!!.itemStack.type.isAir } }

            var pointer = Pair(1,1)
            var page = gui.newPage()
            items.forEach {
                page.chest.set(pointer.first,pointer.second,it)

                pointer = Pair(pointer.first + 1,pointer.second)
                if(pointer.first > 9){
                    pointer = Pair(1,pointer.second + 1)
                }

                if(pointer.second > page.chest.cols){
                    pointer = Pair(1,1)
                    page = gui.newPage()
                }
            }
        }

        private fun getAddPoint(gui: PagedChestGUI): Pair<GUIPage, Pair<Int, Int>>? {
//            var page: GUIPage? = null
//            var pos: Pair<Int, Int> = Pair(1, 1)
//            gui.list.forEach {
//                for (y in (1..it.chest.cols).reversed()) {
//                    for (x in (1..9).reversed()) {
//                        val t = it.chest.get(x, y)
//                        if (t == null || t.itemStack.type.isAir) {
//                            pos = Pair(x, y)
//                            page = it
//                        }
//                    }
//                }
//            }
//            if (page == null) return null
//            return Pair(page!!, pos)
            var pos: Pair<GUIPage, Pair<Int, Int>>? = null
            gui.list.asReversed()
                .filter { it.chest.list.size() != it.chest.cols * 9 }
                .forEach { p ->
                    for (y in (1..p.chest.cols).reversed()) {
                        for (x in (1..9).reversed()) {
                            if (p.chest.get(x, y) == null || p.chest.get(x, y)!!.itemStack.type.isAir) {
                                pos = Pair(p, Pair(x, y))
                            }
                        }
                    }
                }
            return pos
        }
    }

    val list = mutableListOf<GUIPage>()
    private var index = 0
    val chest = ChestGUI(owning, name, cols, plugin)

    fun getPage() = list[index]

    fun getPage(i: Int): GUIPage? {
        return list.getOrNull(i)
    }

    fun setPage(i: Int, page: GUIPage) {
        list[i] = page
    }

    fun newPage(): GUIPage {
        val page = GUIPage(ChestGUI(owning, name, cols - 1, plugin))
        list.add(page)
        return page
    }

    fun copyPage() {
        val page = getPage()
        chest.clear()
        for (x in 1..9) {
            for (y in 1..page.chest.cols) {
                chest.set(x, y, page.chest.get(x, y))
            }
        }
        ui.draw(this, plugin)
//        println("Showing:$index")
    }

    fun next() {
        index++
        if (index > list.lastIndex) {
            index = 0
        }
        copyPage()
    }

    fun before() {
        index--
        if (index < 0) {
            index = list.lastIndex
        }
        copyPage()
    }

    fun open(p: Player) {
        copyPage()
        chest.open(p)
    }

    fun open() = open(owning)
}

class GUIPage(val chest: ChestGUI)

interface PageUIDrawer {
    fun draw(chest: PagedChestGUI, plugin: JavaPlugin)
}

class DefaultPageUI : PageUIDrawer {
    override fun draw(chest: PagedChestGUI, plugin: JavaPlugin) {
        val left = GUIEntry(EasyItemBuilder.genItem(Material.EMERALD_BLOCK, name = "前のページへ"), plugin)
        left.register.add {
            chest.before()
        }
        chest.chest.set(1, chest.chest.cols, left)

        val right = GUIEntry(EasyItemBuilder.genItem(Material.EMERALD_BLOCK, name = "次のページへ"), plugin)
        right.register.add {
            chest.next()
        }
        chest.chest.set(9, chest.chest.cols, right)
    }
}