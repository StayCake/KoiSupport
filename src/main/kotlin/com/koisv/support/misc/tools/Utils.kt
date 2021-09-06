package com.koisv.support.misc.tools

import com.koisv.support.Main.Companion.instance
import hazae41.minecraft.kutils.bukkit.schedule
import net.kyori.adventure.text.Component
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle
import kotlin.math.roundToLong

object Utils {
    private fun toRadian(degree: Double): Double {
        return degree * Math.PI / 180
    }
    fun eulerDegree(x: Double,y: Double,z: Double) : EulerAngle {
        return EulerAngle(toRadian(x),toRadian(y),toRadian(z))
    }
    fun getBlockFace(player: Player): BlockFace? {
        val lastTwoTargetBlocks: List<Block> = player.getLastTwoTargetBlocks(null, 100)
        if (lastTwoTargetBlocks.size != 2 || !lastTwoTargetBlocks[1].type.isOccluding) return null
        val targetBlock: Block = lastTwoTargetBlocks[1]
        val adjacentBlock: Block = lastTwoTargetBlocks[0]
        return targetBlock.getFace(adjacentBlock)
    }
    fun Player.progressBar(time: Float, samework:() -> Unit) {
        samework()
        instance.schedule(true) {
            val oneTick = time / 20
            println(oneTick)
            var currentTick = 0F
            var currentBlocks = 0
            while (currentTick <= time) {
                sendActionBar(
                    Component.text("채집 중 [")
                        .append(
                            Component.text("■".repeat(currentBlocks))
                        )
                        .append(
                            Component.text("□".repeat(20 - currentBlocks) + "] ")
                        )
                        .append(
                            Component.text((((time - currentTick) * 100).roundToLong()) / 100)
                        )
                )
                currentTick += oneTick
                currentBlocks++
                Thread.sleep((oneTick * 1000).toLong())
            }
            sendActionBar(
                Component.text("")
            )
        }
    }
}