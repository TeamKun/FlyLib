package com.flylib.util

class FlatList<T>() {
    private var list: ArrayList<FlatColEntry<T>> = arrayListOf()

    fun set(x: Int, y: Int, t: T) {
        if (isExist(x, y)) {
            getEntry(x, y)!!.t = t
        } else {
            getColForce(x).addEntry(y, t)
        }
    }

    fun setEntry(x: Int, y: Int, t: T) {
        getColForce(x).addEntry(y, t)
    }

    fun getCol(x: Int): FlatColEntry<T>? {
        for (e in list) {
            if (e.x == x) {
                return e
            }
        }
        return null
    }

    fun addCol(x: Int) {
        list.add(FlatColEntry<T>(x))
    }

    fun getColForce(x: Int): FlatColEntry<T> {
        val col = getCol(x)
        if (col != null) return col
        addCol(x)
        return getCol(x)!!
    }

    fun getEntry(x: Int, y: Int): FlatEntry<T>? {
        val col = getCol(x)
        if (col != null) {
            return col.getEntry(y)
        }
        return null
    }

    fun getEntryForce(x: Int, y: Int): FlatEntry<T> {
        val col = getCol(x)
        if (col != null) {
            val e = col.getEntry(y)
            if (e != null) {
                return e
            }
        }
        throw FlatListException("Force Function got null!;not added entry was referred")
    }

    fun isExist(x: Int, y: Int): Boolean {
        return getEntry(x, y) != null
    }
}

class FlatColEntry<T>(var x: Int) {
    var list: ArrayList<FlatEntry<T>> = arrayListOf()
    fun getEntry(y: Int): FlatEntry<T>? {
        for (e in list) {
            if (e.y == y) {
                return e
            }
        }
        return null
    }

    fun addEntry(y: Int, t: T) {
        list.add(FlatEntry(x, y, t))
    }
}

class FlatEntry<T>(var x: Int, var y: Int, var t: T) {
}

class FlatListException(message: String) : Exception(message) {
}

/**
 * The Index Start Point is (0,0)
 * However,Even though,width is 1 and height is 1
 */
class SizedFlatList<K>(val width: NaturalNumber, val height: NaturalNumber) {
    private var flatList = FlatList<K>()
    fun set(x: NaturalNumber, y: NaturalNumber, t: K) {
        outCheck(x, y)
        flatList.setEntry(x.i, y.i, t)
    }

    fun get(x: NaturalNumber, y: NaturalNumber): FlatEntry<K>? {
        outCheck(x, y)
        return flatList.getEntry(x.i, y.i)
    }

    fun getForce(x: NaturalNumber, y: NaturalNumber): FlatEntry<K> {
        outCheck(x, y)
        return flatList.getEntryForce(x.i, y.i)
    }

    fun outCheck(x: NaturalNumber, y: NaturalNumber) {
        if (width - 1 < x.i) {
            throw IndexOutOfSizeException(x.i, y.i, width.i, height.i)
        }
        if (height - 1 < y.i) {
            throw IndexOutOfSizeException(x.i, y.i, width.i, height.i)
        }
    }
}

class IndexOutOfSizeException(private val x: Int, private val y: Int, private val width: Int, private val height: Int) :
    Exception(
        "Index(x:$x,y:$y) is out of (width:$width,height:$height)"
    )