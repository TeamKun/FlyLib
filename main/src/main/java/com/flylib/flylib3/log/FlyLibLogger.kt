package com.flylib.flylib3.log

import com.flylib.flylib3.FlyLib
import com.flylib.flylib3.FlyLibComponent
import com.flylib.flylib3.util.ready
import java.lang.Exception

class FlyLibLogger(override val flyLib: FlyLib) : FlyLibComponent {
    init {
        ready {
            info("FlyLibLogger ready")
        }
    }


    fun fine(s: String) {
        flyLib.plugin.logger.fine(s)
    }

    fun fine(f: (String) -> String) = fine(f("[${flyLib.plugin.name}-${flyLib.plugin.description.version}]"))

    fun info(s: String) {
        flyLib.plugin.logger.info(s)
    }

    fun info(f: (String) -> String) = info(f("[${flyLib.plugin.name}-${flyLib.plugin.description.version}]"))

    fun warn(s: String) {
        flyLib.plugin.logger.warning(s)
    }

    fun warn(f: (String) -> String) = warn(f("[${flyLib.plugin.name}-${flyLib.plugin.description.version}]"))

    fun error(s: String) {
        flyLib.plugin.logger.severe(s)
    }

    fun error(e: Exception) {
        flyLib.plugin.logger.severe(e.localizedMessage)
    }

    fun error(f: (String) -> String) = error(f("[${flyLib.plugin.name}-${flyLib.plugin.description.version}]"))
}