package com.flylib3.toast

import com.flylib3.FlyLib
import com.flylib3.FlyLibComponent
import com.flylib3.util.log
import com.flylib3.util.task
import com.flylib3.util.wait
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import java.lang.IllegalArgumentException
import java.util.*

/**
 * Generate Toast Json
 */
class ToastGenerator(
    val icon: Material,
    val title: String,
    val description: String,
    val background: String = "minecraft:textures/gui/advancements/backgrounds/adventure.png",
    val frame: FrameType = FrameType.Goal,
    val uuid: UUID = UUID.randomUUID()
) {
    fun generateJson(): String {
        return """
            {
                "criteria":{
                    "impossible":{
                        "trigger":"minecraft:impossible"
                    }
                },
                "display":{
                    "icon":{
                        "item":"${icon.key}"
                    },
                    "title":"$title",
                    "description":"$description",
                    "background":"$background",
                    "frame":"${frame.key}",
                    "announce_to_chat":false,
                    "show_toast":true,
                    "hidden":true
                }
            }
        """.trimIndent()
    }

    /**
     * The type of Frame
     * @note just for Toast,this maybe useless or pointless
     */
    enum class FrameType(val key: String) {
        Task("task"), Challenge("challenge"), Goal("goal");
    }
}

class Toast(val generator: ToastGenerator, override val flyLib: FlyLib) : FlyLibComponent {
    fun showTo(p: Player): Boolean {
        return if (loadAdvancement()) {
            grantAdvancement(p)
            task { }.wait(20 * 1).then {
                rollbackAdvancement(p)
                removeAdvancement()
            }.run()
            true
        } else {
            log("[Toast] Advancement not found/error occurred")
            false
        }
    }

    private fun loadAdvancement(): Boolean {
        try {
            Bukkit.getUnsafe()
                .loadAdvancement(NamespacedKey(flyLib.plugin, "toast${generator.uuid}"), generator.generateJson())
            log("[Toast]Advancement Loaded!")
        } catch (_: IllegalArgumentException) {
            // Advancement already loaded
            log("[Toast]Advancement already loaded!/Broken Advancement Json")
            return true
        }
        return true
    }

    private fun removeAdvancement() {
        Bukkit.getUnsafe().removeAdvancement(NamespacedKey(flyLib.plugin, "toast${generator.uuid}"))
        log("[Toast]Advancement Removed!")
    }

    private fun grantAdvancement(p: Player) {
        val advancement = Bukkit.getAdvancement(NamespacedKey(flyLib.plugin, "toast${generator.uuid}"))!!
        p.getAdvancementProgress(advancement).awardCriteria("impossible")
    }

    private fun rollbackAdvancement(p: Player) {
        val advancement = Bukkit.getAdvancement(NamespacedKey(flyLib.plugin, "toast${generator.uuid}"))!!
        p.getAdvancementProgress(advancement).revokeCriteria("impossible")
    }
}
