package com.flylib.gui

import com.flylib.item.EasyItemBuilder
import com.flylib.util.NaturalNumber
import com.flylib.util.SizedFlatList
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ChestGUICollections {
    companion object {
        fun gen(
            p: Player,
            filter: (Material) -> Boolean,
            name: String,
            col: NaturalNumber = NaturalNumber(4)
        ): PagedChestGUI {
            val gui = PagedChestGUI(p, col, name)
            val items = Material.values().filter(filter)
            println("Matched Material:${items.size}")


            var pointer = Pair(1, 1)
            var currentPage = gui.newPage()
            var processed = 0
            items.forEach {
                currentPage.set(
                    NaturalNumber(pointer.first), NaturalNumber(pointer.second),
                    GUIObject(NaturalNumber(pointer.first), NaturalNumber(pointer.second), EasyItemBuilder().genItem(it))
                )

                pointer = Pair(pointer.first + 1, pointer.second)
                if (pointer.first > 9) {
                    // 行またぎ
                    pointer = Pair(1, pointer.second + 1)
                }
                if (pointer.second > col.i) {
                    //ページまたぎ
                    pointer = Pair(pointer.first, 1)
                    currentPage = gui.newPage()
                }

                processed++

            }

            println("Process END!")

            return gui
        }
    }
}

class PagedChestGUI(val p: Player, val col: NaturalNumber, val name: String) {
    val pages: MutableList<ChestGUIPage> = mutableListOf()
    var nowPage = 0
        private set
    var chest = ChestGUI(p, NaturalNumber(col.i + 1), name)

    fun nextPage() {
        nowPage++
        if(nowPage > pages.lastIndex) nowPage -= pages.lastIndex
        drawPage(nowPage)
    }

    fun previousPage() {
        nowPage--
        if (nowPage < 0) nowPage += pages.size
        drawPage(nowPage)
    }

    fun open() {
        drawPage(nowPage)
        chest.open()
    }

    fun clear() {
        chest.p.closeInventory()
        chest = ChestGUI(p, NaturalNumber(col.i + 1), name)
    }

    private fun drawPage(nowPage: Int) {
        clear()
        if(pages.lastIndex < nowPage){
            println("Page Not Found!")
            return
        }
        pages[nowPage].copyToGUI(chest)
        drawUI()
        println("NowPage:$nowPage")
        chest.open()
    }

    private fun drawUI() {
        chest.addGUIObject(
            GUIObject(
                NaturalNumber(1), NaturalNumber(col.i + 1),
                EasyItemBuilder().genItem(Material.EMERALD_BLOCK, "前のページへ")
            ).addCallBack(::cPrev),
            true
        )
        chest.addGUIObject(
            GUIObject(
                NaturalNumber(2), NaturalNumber(col.i + 1),
                EasyItemBuilder().genItem(Material.EMERALD_BLOCK, "リフレッシュ")
            ).addCallBack(::cRef),
            true
        )
        chest.addGUIObject(
            GUIObject(
                NaturalNumber(3), NaturalNumber(col.i + 1),
                EasyItemBuilder().genItem(Material.EMERALD_BLOCK, "次のページへ")
            ).addCallBack(::cNext),
            true
        )
    }

    fun cPrev(e:InventoryClickEvent){
        println("前のページへ")
        previousPage()
    }

    fun cRef(e:InventoryClickEvent){
        println("リフレッシュ")
        drawPage(nowPage)
    }

    fun cNext(e:InventoryClickEvent){
        println("次のページへ")
        nextPage()
    }

    fun newPage(): ChestGUIPage {
        val page = ChestGUIPage(col)
        pages.add(page)
        page.callbacks.add { p, stack -> callbacks.forEach { it(p, stack) } }
        return page
    }

    val callbacks: MutableList<(ChestGUIPage, ItemStack) -> Unit> = mutableListOf()
}

class ChestGUIPage(val col: NaturalNumber) {
    private val map: SizedFlatList<GUIObject> = SizedFlatList(NaturalNumber(9), col)
    var called = 0
    fun copyToGUI(chest: ChestGUI) {
        map.forEach {
            // どうせUI描画で呼ばれるので
            chest.addGUIObject(
                it.t,
                true
            )
            called++
        }
    }

    fun set(x: NaturalNumber, y: NaturalNumber, obj: GUIObject) {
        if (x.i != obj.x.i || y.i != obj.y.i) {
            throw IllegalArgumentException("Position of GUIObject and x,y not matched!")
        } else {
            obj.addCallBack(::onClick)
            map.set(x, y, obj)
        }
    }

    fun get() = map

    fun onClick(inventoryClickEvent: InventoryClickEvent) {
        callbacks.forEach {
            it(this, inventoryClickEvent.currentItem!!)
        }
    }

    val callbacks: MutableList<(ChestGUIPage, ItemStack) -> Unit> = mutableListOf()
}