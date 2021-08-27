package com.koisv.support.misc.extend

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object PlayerExtend {
    fun Player.giveItem(item: ItemStack) {
        if (inventory.itemInMainHand.type == Material.AIR) {
            inventory.setItem(EquipmentSlot.HAND, item)
        } else {
            if (inventory.firstEmpty() == -1) {
                world.dropItem(location,item)
            } else {
                inventory.addItem(item)
            }
        }
    }
    fun Player.giveItem(item: Collection<ItemStack>) {
        item.forEach {
            if (inventory.firstEmpty() == -1) {
                world.dropItem(location,it)
            } else {
                inventory.addItem(it)
            }
        }
    }
}