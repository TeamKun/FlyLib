package com.flylib.flylib3.command

import com.flylib.flylib3.util.Tree
import kotlin.reflect.KType

abstract class Usage {
    /**
     * @return the tree of usage structure
     */
    abstract fun getStringTree(): Tree<String>

    /**
     * @return the tree of type usage structure
     */
    abstract fun getTypeTree(): Tree<KType>
}