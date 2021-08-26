package com.koisv.support.jobs

import com.koisv.support.Main.Companion.instance
import com.koisv.support.misc.extend.PlayerExtend.giveItem
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.schedule
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle


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
                e.player.world.spawnParticle(Particle.BLOCK_DUST,location.toCenterLocation(),200,e.block.blockData)
                e.player.playSound(Sound.sound(Key.key("block.wood.break"), Sound.Source.BLOCK,1F,0.8F),location.x,location.y,location.z)
                val tool = e.itemInHand
                e.player.inventory.remove(tool)
                val lookFace = getBlockFace(e.player)?.name
                println(lookFace)
                e.isCancelled = true
                val actionStand : ArmorStand = e.player.world.spawnEntity(
                    e.block.location.add(0.0,-1.0,-0.7),EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM
                ) {
                    val actionStand = it as ArmorStand
                    actionStand.isVisible = false
                    actionStand.setGravity(false)
                    actionStand.setArms(true)
                    actionStand.setItem(EquipmentSlot.HAND,tool)
                    actionStand.rightArmPose = EulerAngle(80.0,0.0,190.0)
                } as ArmorStand
                instance.schedule (false,1) {
                    actionStand.remove()
                    if (e.itemInHand.type == Material.AIR) e.player.inventory.setItem(EquipmentSlot.HAND,tool)
                    else e.player.giveItem(tool)
                }
            }
        }
        private fun getBlockFace(player: Player): BlockFace? {
            val lastTwoTargetBlocks: List<Block> = player.getLastTwoTargetBlocks(null, 100)
            if (lastTwoTargetBlocks.size != 2 || !lastTwoTargetBlocks[1].type.isOccluding) return null
            val targetBlock: Block = lastTwoTargetBlocks[1]
            val adjacentBlock: Block = lastTwoTargetBlocks[0]
            return targetBlock.getFace(adjacentBlock)
        }
    }
}
