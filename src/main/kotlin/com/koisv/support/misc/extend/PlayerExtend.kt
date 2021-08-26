package com.koisv.support.misc.extend

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object PlayerExtend {
    fun Player.giveItem(item: ItemStack) {
        if (inventory.firstEmpty() == -1) {
            world.dropItem(location,item)
        } else {
            inventory.addItem(item)
        }
    }
}