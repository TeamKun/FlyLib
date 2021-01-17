package com.flylib

import com.flylib.util.FlatList

fun main(args:Array<String>){
    Test().FlatListTest()
}

class Test{
    @Suppress("FunctionName")
    fun FlatListTest(){
        // PASSED!
        val list = FlatList<String>()
        list.set(1,1,"1,1")
        list.set(2,1,"2,1")
        list.set(1,3,"1,3")
        print("2,1:")
        println(list.getEntryForce(2,1).t)
    }
}