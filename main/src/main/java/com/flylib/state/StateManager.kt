package com.flylib.state

import org.bukkit.entity.Player

class State(){
    private var players:HashMap<Player,StateManager> = hashMapOf()
    private val global = StateManager()

    fun player(p:Player): StateManager {
        if(players.containsKey(p)){
            return players[p]!!
        }
        players[p] = StateManager()
        return players[p]!!
    }

    fun global() = global
}

class StateManager {
    private val map:HashMap<String,IState<*>> = hashMapOf()

    fun get(id:String):IState<*>?{
        return if (map.containsKey(id)){
            map[id]
        }else{
            null
        }
    }

    fun<T> setState(id:String,t:IState<T>){
        map[id] = t
    }

    fun<T> set(id:String,t:T){
        setState(id,IState<T>(t))
    }
}

class IState<T>(private var t:T) {
    fun get(): T = t
    fun set(t: T) {this.t=t}
}