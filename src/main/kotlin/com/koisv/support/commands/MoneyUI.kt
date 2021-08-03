package com.koisv.support.commands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class MoneyUI {
    companion object
    fun open(e: InventoryClickEvent) {

    }
    fun makeskull(p: Player) : GuiItem{
        return GuiItem(
            ItemStack(Material.PLAYER_HEAD).apply {
                itemMeta = itemMeta.apply {
                    val skull = this as SkullMeta
                    skull.owningPlayer = p
                    displayName(p.displayName())
                    lore()
                }
            }
        )
    }
}