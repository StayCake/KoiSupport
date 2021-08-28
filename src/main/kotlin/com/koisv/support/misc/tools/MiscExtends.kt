package com.koisv.support.misc.tools

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

object MiscExtends {
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
    fun ItemStack.useTool(p: Player) {
        this.apply {
            itemMeta = itemMeta.apply {
                val unBreaking = this@useTool.getEnchantmentLevel(Enchantment.DURABILITY)
                if (Math.random() * 100 <= 100/(unBreaking + 1) && p.gameMode != GameMode.CREATIVE) {
                    if (this is Damageable) {
                        damage += 1
                        if (this@useTool.type.maxDurability <= damage) {
                            p.playSound(
                                Sound.sound(
                                    Key.key("entity.item.break"),
                                    Sound.Source.MASTER,1F,1F
                                )
                            )
                            this@useTool.type = Material.AIR
                        }
                    }
                }
            }
        }
    }
}