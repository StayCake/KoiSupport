package com.koisv.support.misc.extend

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

object ItemStackExtend {
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