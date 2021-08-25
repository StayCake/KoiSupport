package com.koisv.support.jobs

import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class WoodCutter {
    companion object {
        private val axe = listOf(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.GOLDEN_AXE,
            Material.IRON_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
        )

        fun jobWorks(e: BlockPlaceEvent) {
            if (e.blockReplacedState.type == Material.OXIDIZED_CUT_COPPER_SLAB) {
                val mh = e.player.inventory.itemInMainHand
                val oh = e.player.inventory.itemInOffHand
                when {
                    axe.contains(mh.type) -> {
                        if (oh == ItemStack(Material.DIAMOND,oh.amount)) {
                            e.isCancelled = true
                            oh.amount -= 1
                            mh.addUnsafeEnchantment(Enchantment.DIG_SPEED, (mh.enchantments[Enchantment.DIG_SPEED] ?: 0) + 1)
                            e.player.msg("test")
                            e.player.msg("강화!")
                        } else {
                            e.player.msg("강화 재료가 없습니다.")
                        }
                    }
                }
            }
        }
        fun jobWorks(e: BlockDamageEvent) {
            val mh = e.itemInHand
            if (axe.contains(mh.type)) {
                val location = e.block.location
                e.isCancelled = true
                e.player.world.spawnParticle(Particle.BLOCK_DUST,location.toCenterLocation(),200,e.block.blockData)
                e.player.playSound(Sound.sound(Key.key("block.wood.break"), Sound.Source.BLOCK,1F,0.8F),location.x,location.y,location.z)
            }
        }
    }
}