package com.koisv.support.yaml

import com.koisv.support.Main

class Stats {
    companion object {
        fun read(loc: String,type: Any?) : Any?{
            return Main.stats.get(loc,type)
        }
        fun write(loc: String,data: Any?) {
            Main.stats.set(loc,data)
            Main.stats.save(Main.statsloc)
        }
    }
}