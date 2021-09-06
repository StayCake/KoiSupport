package com.koisv.support.jobs

import com.koisv.support.Main.Companion.instance
import com.koisv.support.misc.tools.Utils.eulerDegree
import com.koisv.support.misc.tools.Utils.progressBar
import hazae41.minecraft.kutils.bukkit.schedule
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.EquipmentSlot

class Herbalist {
    companion object {
        val blades = listOf(
            Material.WOODEN_SHOVEL,
            Material.STONE_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.IRON_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL
        )
        fun jobWorks(e: BlockDamageEvent) {
            if (e.block.type == Material.GRASS_BLOCK && blades.contains(e.itemInHand.type)) {
                e.isCancelled
                e.player.progressBar(1.5F) {
                    e.isCancelled = true
                    val blade : ArmorStand = e.player.world.spawnEntity(
                        e.block.location.toBlockLocation().add(0.0,0.3,0.0)
                        , EntityType.ARMOR_STAND
                        , CreatureSpawnEvent.SpawnReason.CUSTOM
                    ) {
                        val stands = it as ArmorStand
                        stands.isInvulnerable = true
                        stands.isVisible = false
                        stands.setArms(true)
                        stands.setGravity(false)
                        stands.setCanMove(false)
                        stands.addEquipmentLock(EquipmentSlot.HAND,ArmorStand.LockType.REMOVING_OR_CHANGING)
                        stands.setItem(EquipmentSlot.HAND,e.itemInHand)
                        stands.rightArmPose = eulerDegree(90.0,0.0,0.0)
                    } as ArmorStand
                    instance.schedule(false,1) {
                        blade.remove()
                    }
                    /*val yaw = e.player.location.yaw
                    if (yaw >= -135 && yaw < -45) {

                    } else if (yaw >= -45 && yaw < 45) {

                    } else if (yaw >= 45 && yaw < 135) {

                    } else {

                    }*/
                }
            }
        }
    }
}