package com.flylib.sign

import com.flylib.color.IColor
import org.bukkit.block.Block
import org.bukkit.block.Sign
import java.lang.Exception

class EasySign (val sign:Sign){
    companion object{
        fun get(block:Block):EasySign?{
            if(block.state is Sign){
                return EasySign(block.state as Sign)
            }
            return null
        }
    }

    /**
     * Top is line 0
     * Bottom is line 3
     */
    fun editLine(line:Int,s:String){
        if(line in 0..3){
            sign.setLine(line,s)
            update()
        }else throw EasySignException(line,sign.lines.size,s)
    }

    fun getLine(line:Int): String {
        return sign.getLine(line)
    }

    fun setColor(color: IColor){
        if(color.getDyeColor()!=null){
            sign.color = color.getDyeColor()
        }
    }

    fun update(){
        if(!sign.update())
            println("Some Update didn't went good in EasySign")
    }
}

class EasySignException(line:Int,size:Int,s:String):Exception("Editing line:$line String:$s is out of $size"){
}