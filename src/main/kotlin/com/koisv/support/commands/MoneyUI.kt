package com.koisv.support.commands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.math.ceil

class MoneyUI {
    companion object
    fun open(e: InventoryClickEvent) {
        val players = Bukkit.getOnlinePlayers()
        var length = ceil(players.size.toFloat() / 9)
        if (length > 6) length = 6F
        val payselect = ChestGui(length.toInt(),"대상 선택")
        val paypane = StaticPane(9,length.toInt())
        if (length <= 6) {
            var index = 0
            var line = 0
            players.forEach { it2 ->
                if (index > 8) {
                    index = 0
                    line += 1
                }
                paypane.addItem(makeskull(it2) {
                    amount(it2)
                },index,line)
                index += 1
            }
        }
        paypane.isVisible = true
        payselect.addPane(paypane)
        payselect.show(e.whoClicked)
    }
    private fun makeskull(p: Player,event:(ice: InventoryClickEvent) -> Unit) : GuiItem{
        return GuiItem(
            ItemStack(Material.PLAYER_HEAD).apply {
                itemMeta = itemMeta.apply {
                    val skull = this as SkullMeta
                    skull.owningPlayer = p
                    displayName(p.displayName())
                    lore()
                }
            }
        ){
            event(it)
        }
    }
    private fun amount(p: Player) {

    }
}