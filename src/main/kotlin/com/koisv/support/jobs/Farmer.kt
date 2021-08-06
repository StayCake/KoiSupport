package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import org.bukkit.entity.Player

class Farmer {
    companion object {
        fun farmgui(p: Player) : ChestGui {
            val farmShop = ChestGui(6, "농산품 상점")
            farmShop.title = p.customName.toString()
            return farmShop
        }
    }
}