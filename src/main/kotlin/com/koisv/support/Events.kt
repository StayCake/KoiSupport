package com.koisv.support

import com.koisv.support.jobs.*
import com.koisv.support.misc.tools.Shops
import hazae41.minecraft.kutils.bukkit.listen
import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

class Events(main: Main) {
    init {
        main.listen<PlayerFishEvent> {
            Fisher.jobWorks(it)
        }
        main.listen<BlockPlaceEvent> {
            WoodCutter.jobWorks(it)
            if (it.block.type == Material.SUGAR_CANE) Main.placeCheck.add(it.block)
        }
        main.listen<BlockDamageEvent> {
            WoodCutter.jobWorks(it)
            Herbalist.jobWorks(it)
        }
        main.listen<PlayerHarvestBlockEvent> {
            Farmer.expWorks(it)
        }
        main.listen<BlockBreakEvent> {
            Miner.expWorks(it)
            Farmer.expWorks(it)
        }
        main.listen<PlayerInteractEntityEvent> {
            Shops.rightClick(it)
        }
        main.listen<EntityDamageByEntityEvent> {
            Shops.leftClick(it)
        }
    }
}