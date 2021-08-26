package com.koisv.support.misc.tools

import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

object Utils {
    fun getBlockFace(player: Player): BlockFace? {
        val lastTwoTargetBlocks: List<Block> = player.getLastTwoTargetBlocks(null, 100)
        if (lastTwoTargetBlocks.size != 2 || !lastTwoTargetBlocks[1].type.isOccluding) return null
        val targetBlock: Block = lastTwoTargetBlocks[1]
        val adjacentBlock: Block = lastTwoTargetBlocks[0]
        return targetBlock.getFace(adjacentBlock)
    }
}