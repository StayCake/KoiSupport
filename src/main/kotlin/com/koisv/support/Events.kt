package com.koisv.support

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.jobs.Farmer
import com.koisv.support.jobs.Fisher
import com.koisv.support.jobs.Miner
import com.koisv.support.jobs.WoodCutter
import com.koisv.support.tools.Instance.Companion.customshop
import com.koisv.support.tools.Shops.Companion.shopItem
import hazae41.minecraft.kutils.bukkit.keys
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.section
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerHarvestBlockEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

class Events : Listener {

    @EventHandler
    fun jobWork(e: PlayerFishEvent) {
        Fisher.jobWorks(e)
    }
    @EventHandler
    fun jobWork(e: BlockPlaceEvent) {
        WoodCutter.jobWorks(e)
        if (e.block.type == Material.SUGAR_CANE) Main.placeCheck.add(e.block)
    }

    @EventHandler
    fun expWork(e: PlayerHarvestBlockEvent) {
        Farmer.expWorks(e)
    }
    @EventHandler
    fun expWork(e: BlockBreakEvent) {
        Miner.expWorks(e)
        Farmer.expWorks(e)
    }

    @EventHandler
    fun interactNPC(e: PlayerInteractEntityEvent) {
        val eh: EquipmentSlot = e.hand
        val custom = customshop.keys.contains(e.rightClicked.name)
        if (eh == EquipmentSlot.HAND) {
            when (e.rightClicked.name) {
                "§b어부" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |§e어부 §7≫ §f안녕하세요!
                        |§e어부 §7≫ §f팔 물건을 들고 좌클릭하세요.
                        |§e어부 §7≫ §f웅크리고 있다면 한번에 팔 수 있어요. [보물 제외]
                        |§e어부 §7≫ §f아, 낚시용품만 받아요. 그리고 물건을 사고 싶으면 웅크리고 불러주세요!""".trimMargin()
                        )
                    } else {
                        val gui = Fisher.fishGui(e.player)
                        gui.show(e.player)
                    }
                }
                "§7광부" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |§7광부 §7≫ §f안녕 친구!
                        |§7광부 §7≫ §f팔 물건을 들고 좌클릭하라고.
                        |§7광부 §7≫ §f웅크리고 있다면 한번에 팔 수 있다네.
                        |§7광부 §7≫ §f광물만 받는다네. 그리고 물건을 사고 싶으면 웅크리고 불러주게!""".trimMargin()
                        )
                    } else {
                        val gui = Miner.mineGui(e.player)
                        gui.show(e.player)
                    }
                }
                "§7수중 광부" -> {
                    e.player.msg(
                        """
                        |§7광부 §7≫ §f안녕하신가!
                        |§7광부 §7≫ §f이쯤까지 왔으니 이곳을 잘 알지 않는가.
                        |§7광부 §7≫ §f여기서부턴 물건 값이 달라진다는 것만 잘 알아두고,
                        |§7광부 §7≫ §f이젠 불러만 주게.""".trimMargin()
                    )
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "§7폐광 광부" -> {
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "§7심층 광부" -> {
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "§a농부" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |§e농부 §7≫ §f여어!
                        |§e농부 §7≫ §f팔 물건이 있으면 주라고.
                        |§e농부 §7≫ §f웅크리고 있다면 한번에 받아 주지.
                        |§e농부 §7≫ §f농산물만 줘. 웅크리고 우클릭해서 물건을 봐도 좋고!""".trimMargin()
                        )
                    } else {
                        val gui = Farmer.farmGui(e.player)
                        gui.show(e.player)
                    }
                }
                else -> {
                    if (custom) {
                        val t = customshop.section(e.rightClicked.name)?.getKeys(false)
                        println(t)
                        if (t != null) {
                            val cShop = ChestGui(ceil((t.size.toFloat()) /9).toInt(), e.rightClicked.name)
                            val cPane = StaticPane(9,ceil((t.size.toFloat()) /9).toInt())
                            var index = 0
                            var line = 0
                            t.forEach { m ->
                                if (index > 8) {
                                    index = 0
                                    line++
                                }
                                val tm = Material.getMaterial(m)
                                if (tm != null) {
                                    cPane.addItem(shopItem(e.player,0,null, customshop.section(e.rightClicked.name)?.getInt(m) ?: 0,null,"shop",tm,null),index,line)
                                }
                                index++
                            }
                            cShop.addPane(cPane)
                            cShop.show(e.player)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun clickNPC(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.PLAYER) {
            val p = e.damager as Player
            val pmh = p.inventory.itemInMainHand
            val fish = p.hasPermission("fisher.fish")
            val mine = p.hasPermission("miner.mine")
            var finalCost = 0
            var check = false
            var target = ""
            when (e.entity.name) {
                "${ChatColor.AQUA}어부" -> {
                    target = "§e어부"
                    if (p.isSneaking){
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val item: ItemStack = it
                                val mi = i.contents.indexOf(item)
                                val amt = item.amount
                                val cost = Fisher.getFishCost(item.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (fish) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val im = pmh.itemMeta
                        val cost = Fisher.getFishCost(pmh.type)
                        when (im){
                            Fisher.fishTreasure.itemMeta -> {
                                check = true
                                Fisher.fishTreasure(p,pmh.amount)
                            }
                            Fisher.fishTrash.itemMeta -> {
                                check = true
                                Fisher.fishTrash(p,pmh.amount)
                            }
                            else -> {
                                if (cost > 0) {
                                    p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                                    finalCost += if (fish) (cost * amt * 1.05).toInt() else cost * amt
                                }
                            }
                        }
                    }
                }
                "${ChatColor.GRAY}광부" -> {
                    target = "§7광부"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = Miner.getMine1Cost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine1Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                "${ChatColor.GRAY}수중 광부" -> {
                    target = "§7광부"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = Miner.getMine2Cost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine2Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                "${ChatColor.GRAY}폐광 광부" -> {
                    target = "§7광부"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = Miner.getMine3Cost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine3Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                "${ChatColor.GRAY}심층 광부" -> {
                    target = "§7광부"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = Miner.getMine4Cost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine4Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                "${ChatColor.GREEN}농부" -> {
                    target = "§e농부"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = Farmer.getCropCost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Farmer.getCropCost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                else -> check = true
            }
            if (!check) {
                if (finalCost == 0) {
                    p.msg("$target §7≫ §f판매 가능한 물건만 주세요...")
                } else {
                    econ?.depositPlayer(p, finalCost.toDouble())
                    p.msg("$target §7≫ §f${finalCost}원에 받을게요.")
                }
            }
        }
    }

}