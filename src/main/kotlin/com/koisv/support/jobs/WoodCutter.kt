package com.koisv.support.jobs

import com.koisv.support.Main.Companion.instance
import com.koisv.support.Main.Companion.woodDamage
import com.koisv.support.Main.Companion.woodNow
import com.koisv.support.Main.Companion.woodOwner
import com.koisv.support.Main.Companion.woodTime
import com.koisv.support.misc.tools.MiscExtends.giveItem
import com.koisv.support.misc.tools.MiscExtends.useTool
import com.koisv.support.misc.tools.Stats.setStat
import com.koisv.support.misc.tools.Stats.showStat
import com.koisv.support.misc.tools.Utils.eulerDegree
import com.koisv.support.misc.tools.Utils.getBlockFace
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.schedule
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
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
import java.time.LocalTime


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

        private val woods = listOf(
            Material.OAK_LOG,
            Material.DARK_OAK_LOG,
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.SPRUCE_LOG
        )

        private val woodMax = mapOf(
            Material.NETHERITE_AXE to 2,
            Material.DIAMOND_AXE to 3,
            Material.IRON_AXE to 4,
            Material.STONE_AXE to 5,
            Material.WOODEN_AXE to 6,
            Material.GOLDEN_AXE to 6,
        )

        private fun Player.jobExp() {
            this.setStat(1,"WoodCut")
            this.showStat("WoodCut")
        }
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
            val tool = e.itemInHand
            val owner = woodOwner[e.block]
            val damage = woodDamage[e.block]
            if (owner != null && owner != e.player) {
                if (axe.contains(tool.type)) {
                    e.isCancelled = true
                    e.player.msg("&c시스템 &7>> 타인의 나무를 가로챌 수 없습니다!")
                    instance.schedule(true) {
                        e.player.inventory.remove(tool)
                        Thread.sleep(500)
                        e.player.giveItem(tool)
                    }
                }
            } else if (e.player.world == Bukkit.getWorld("world")) {
                if (owner == null) woodOwner[e.block] = e.player
                fun playerSide(main: Player, target: ArmorStand) {
                    val yaw = main.location.yaw
                    if (yaw >= -135 && yaw < -45) {
                        println(1)
                        target.setRotation(90.0F, 0.0F)
                        target.teleport(target.location.add(-1.65, 0.0, -1.15))
                    } else if (yaw >= -45 && yaw < 45) {
                        target.setRotation(180.0F, 0.0F)
                        target.teleport(target.location.add(-0.5, 0.0, -2.7))
                    } else if (yaw >= 45 && yaw < 135) {
                        target.setRotation(270.0F, 0.0F)
                        target.teleport(target.location.add(1.1, 0.0, -1.65))
                    }
                }
                fun sidePose(side: BlockFace, target: ArmorStand) {
                    target.rightArmPose = eulerDegree(270.0, 0.0, 270.0)
                    when (side) {
                        BlockFace.WEST -> {
                            target.setRotation(180.0F, 0.0F)
                            target.teleport(target.location.add(-1.7, 0.0, 2.4))
                        }
                        BlockFace.NORTH -> {
                            target.setRotation(270.0F, 0.0F)
                            target.teleport(target.location.add(-2.1, 0.0, 0.3))
                        }
                        BlockFace.SOUTH -> {
                            target.setRotation(90.0F, 0.0F)
                            target.teleport(target.location.add(0.4, 0.0, 2.0))
                        }
                        BlockFace.UP -> {
                            playerSide(e.player, target)
                            target.rightArmPose = eulerDegree(135.0, 0.0, 180.0)
                            target.teleport(target.location.add(-0.6, 0.6, 2.6))
                        }
                        BlockFace.DOWN -> {
                            playerSide(e.player, target)
                            target.rightArmPose = eulerDegree(135.0, 0.0, 0.0)
                            target.teleport(target.location.add(-0.6, -0.8, 2.6))
                        }
                        else -> {
                        }
                    }
                }

                if (axe.contains(tool.type) && woods.contains(e.block.type) && !woodNow.contains(e.block)) {
                    val location = e.block.location
                    e.player.world.spawnParticle(
                        Particle.BLOCK_DUST,
                        location.toCenterLocation(),
                        200,
                        e.block.blockData
                    )
                    e.player.playSound(
                        Sound.sound(Key.key("block.wood.break"), Sound.Source.BLOCK, 1F, 0.8F),
                        location.x,
                        location.y,
                        location.z
                    )
                    e.player.inventory.remove(tool)
                    val lookFace = getBlockFace(e.player)
                    e.isCancelled = true
                    val actionStand: ArmorStand = e.player.world.spawnEntity(
                        e.block.location.add(1.35, -0.75, -0.7),
                        EntityType.ARMOR_STAND,
                        CreatureSpawnEvent.SpawnReason.CUSTOM
                    ) {
                        val actionStand = it as ArmorStand
                        actionStand.isVisible = false
                        actionStand.isInvulnerable = true
                        actionStand.setCanMove(false)
                        actionStand.setGravity(false)
                        actionStand.setArms(true)
                        actionStand.setItem(EquipmentSlot.HAND, tool)
                        if (lookFace != null) sidePose(lookFace, actionStand)
                    } as ArmorStand
                    e.player.sendActionBar(Component.text("나무 : ${(woodDamage[e.block] ?: 0) + 1} / ${woodMax[tool.type]}"))
                    woodNow.add(e.block)
                    instance.schedule(true) {
                        Thread.sleep(
                            when (tool.getEnchantmentLevel(Enchantment.DIG_SPEED)) {
                                1 -> 800
                                2 -> 700
                                3 -> 500
                                else -> 1000
                            }
                        )
                        instance.schedule (false) {
                            woodNow.remove(e.block)
                            actionStand.remove()
                            tool.useTool(e.player)
                            if (damage == woodMax[tool.type]?.minus(1)) {
                                woodOwner.remove(e.block)
                                woodDamage.remove(e.block)
                                e.player.giveItem(e.block.drops)
                                e.player.jobExp()
                            } else {
                                val currentDamage = woodDamage[e.block]
                                woodDamage[e.block] = (currentDamage ?: 0) + 1
                                woodTime[e.block] = LocalTime.now()
                                instance.schedule(true, 10) {
                                    Thread.sleep(1)
                                    val now = LocalTime.now()
                                    if (woodTime[e.block]?.plusSeconds(10)?.isBefore(now) == true) {
                                        woodOwner.remove(e.block)
                                        woodDamage.remove(e.block)
                                        woodTime.remove(e.block)
                                    }
                                }
                            }
                            e.player.giveItem(tool)
                        }
                    }
                } else if (woodNow.contains(e.block)) {
                    instance.schedule(true) {
                        e.player.inventory.remove(tool)
                        Thread.sleep(500)
                        e.player.giveItem(tool)
                    }
                }
            }
        }
    }
}
