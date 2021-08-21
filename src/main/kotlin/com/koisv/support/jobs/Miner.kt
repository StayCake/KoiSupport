package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.gui.type.FurnaceGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.Main
import com.koisv.support.econ
import com.koisv.support.tools.Instance
import com.koisv.support.tools.Shops.Companion.shopItem
import com.koisv.support.tools.Stats
import com.koisv.support.ui.GameUI
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.math.round

class Miner {
    companion object {
        private val pickaxe = listOf(
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE
        )
        private val nonExpOre = listOf(
            Material.COPPER_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.ANCIENT_DEBRIS
        )
        private fun repair(p: Player) : FurnaceGui {
            val main = FurnaceGui("도구 수리하기")
            main.setOnBottomClick {
                if (it.isShiftClick) it.isCancelled = true
            }
            main.setOnTopClick { its ->
                its.isCancelled = true
                if (its.slot == 0) {
                    if (its.cursor?.itemMeta is Damageable) {
                        var save = its.cursor as ItemStack
                        val inPane = StaticPane(1,1)
                        val fuelPane = StaticPane(1,1)
                        val reItem = GuiItem(its.cursor as ItemStack)
                        val repairBook = GuiItem(
                            ItemStack(Material.ANVIL).apply {
                                val level = Stats.getStat(p,"Mine")
                                val cost = when {
                                    level > 40 -> 4500000
                                    level > 25 -> 1500000
                                    level > 15 -> 600000
                                    level > 7 -> 150000
                                    else -> if (its.cursor?.type == Material.GOLDEN_PICKAXE) 25000 else 45000
                                }
                                itemMeta = itemMeta.apply {
                                    displayName(
                                        Component.text("수리하기")
                                            .decoration(TextDecoration.ITALIC,false)
                                    )
                                    lore(
                                        if (econ?.has(p,cost.toDouble()) == true) {
                                            listOf(
                                                Component.text("수리 비용 : $cost 원")
                                                    .color(TextColor.color(255, 255, 255))
                                                    .decoration(TextDecoration.ITALIC,false),
                                                Component.text("수리 미니게임을 하려면 클릭하세요.")
                                                    .color(TextColor.color(255, 255, 255))
                                                    .decoration(TextDecoration.ITALIC,false),
                                                Component.text("점수에 따라 수리 정도가 달라집니다.")
                                                    .color(TextColor.color(255, 255, 255))
                                                    .decoration(TextDecoration.ITALIC,false),
                                                Component.text("연습게임은 /미니게임 으로 할 수 있습니다.")
                                                    .color(TextColor.color(255, 255, 255))
                                                    .decoration(TextDecoration.ITALIC,false)
                                            )
                                        } else {
                                            listOf(
                                                Component.text("수리 비용 : $cost 원")
                                                    .color(TextColor.color(255, 0, 0))
                                                    .decorate(TextDecoration.UNDERLINED)
                                                    .decoration(TextDecoration.ITALIC,false),
                                                Component.text("수리 비용이 부족합니다.")
                                                    .color(TextColor.color(255, 255, 255))
                                                    .decoration(TextDecoration.ITALIC,false)
                                            )
                                        }
                                    )
                                }
                            }
                        ) {
                            val level = Stats.getStat(p,"Mine")
                            val cost = when {
                                level > 40 -> 4500000
                                level > 25 -> 1500000
                                level > 15 -> 600000
                                level > 7 -> 150000
                                else -> if (save.type == Material.GOLDEN_PICKAXE) 25000 else 45000
                            }
                            if (econ?.has(p,cost.toDouble()) == true) {
                                econ?.withdrawPlayer(p,cost.toDouble())
                                GameUI.game(p) { score ->
                                    val apply = when {
                                        level > 40 -> score * 1.5
                                        level > 25 -> score
                                        level > 15 -> score * 0.2
                                        level > 7 -> score * 0.1
                                        else -> score * 0.05
                                    }
                                    p.inventory.addItem(save.apply {
                                        itemMeta = itemMeta.apply {
                                            val d = this as Damageable
                                            if (d.damage < apply.toInt()) {
                                                d.damage = 0
                                            } else d.damage -= apply.toInt()
                                        }
                                    })
                                    p.msg("수리 완료!")
                                }
                            } else {
                                p.msg("소지금이 부족합니다.")
                            }
                        }
                        var ex = 0
                        fuelPane.addItem(repairBook,0,0)
                        inPane.addItem(reItem,0,0)
                        reItem.setAction {
                            if (its.cursor?.type == Material.AIR && ex > 0) {
                                it.whoClicked.setItemOnCursor(it.currentItem)
                                inPane.removeItem(0,0)
                                fuelPane.removeItem(0,0)
                                main.update()
                            }
                            ex++
                        }
                        its.whoClicked.setItemOnCursor(null)
                        main.setOnClose {
                            if (it.reason != InventoryCloseEvent.Reason.OPEN_NEW) {
                                p.inventory.addItem(save)
                                save = ItemStack(Material.AIR)
                            }
                        }
                        main.ingredientComponent.addPane(inPane)
                        main.fuelComponent.addPane(fuelPane)
                        main.update()
                    }
                }
            }
            return main
        }

        fun mineGui(p: Player) : ChestGui {
            val mineShop = ChestGui(3,"§b광산용품 상점")
            mineShop.setOnGlobalClick {
                if (it.isShiftClick) it.isCancelled = true
            }
            mineShop.setOnTopClick {
                it.isCancelled = true
            }
            mineShop.setOnGlobalDrag {
                it.isCancelled = true
            }
            val p1 = shopItem(p,0,"부서진 곡괭이",50000,"\"아니, 캐지기는 하는 거야?\"","Mine",Material.WOODEN_PICKAXE)
            val p2 = shopItem(p,2,"닳은 곡괭이",30000,"가벼워서 부서질 수준이다.","Mine",Material.GOLDEN_PICKAXE)
            val p3 = shopItem(p,7,"중고 곡괭이",200000,"그나마 쓸 만한 녀석이다.","Mine",Material.STONE_PICKAXE)
            val p4 = shopItem(p,15,"곡괭이",750000,"철물점에서 본 듯한 녀석이다.","Mine",Material.IRON_PICKAXE)
            val p5 = shopItem(p,25,"전문 곡괭이",2000000,"관리되고 있는 녀석이라고 한다.","Mine",Material.DIAMOND_PICKAXE)
            val p6 = shopItem(p,40,"장인 곡괭이",5000000,"직접 갈고닦은 녀석이라고 한다.","Mine",Material.NETHERITE_PICKAXE)
            val md = shopItem(p, 35, Enchantment.MENDING, 1, "수선", 1000000, "이젠 무한의 시대.","Mine", pickaxe)
            val u1 = shopItem(p, 2, Enchantment.DURABILITY, 1, "내구성 I", 30000,"자그마한 납땜.","Mine", pickaxe)
            val u2 = shopItem(p, 6, Enchantment.DURABILITY, 2, "내구성 II", 70000,"철판 덧대기.","Mine", pickaxe)
            val u3 = shopItem(p, 18, Enchantment.DURABILITY, 3, "내구성 III", 150000,"망치질 추가하기.","Mine", pickaxe)
            val l1 = shopItem(p, 5, Enchantment.LOOT_BONUS_BLOCKS, 1, "행운 I", 85000,"네잎 클로버를 찾았다.","Mine", pickaxe)
            val l2 = shopItem(p, 12, Enchantment.LOOT_BONUS_BLOCKS, 2, "행운 II", 190000,"행운이 함께하길 빌었다.","Mine", pickaxe)
            val l3 = shopItem(p, 21, Enchantment.LOOT_BONUS_BLOCKS, 3, "행운 III", 320000,"신이 도와주길 바랬다.","Mine", pickaxe)
            val e1 = shopItem(p, 7, Enchantment.DIG_SPEED, 1, "효율 I",100000, "조금은 가벼워진 듯 하다.","Mine", pickaxe)
            val e2 = shopItem(p, 14, Enchantment.DIG_SPEED, 2, "효율 II",240000, "한 손으로 들만하다.","Mine", pickaxe)
            val e3 = shopItem(p, 26, Enchantment.DIG_SPEED, 3, "효율 III",380000, "종이 몇 장 수준이다.","Mine", pickaxe)
            val rp = GuiItem(
                ItemStack(Material.GRINDSTONE).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("수리하기")
                                .decoration(TextDecoration.ITALIC,false)
                        )
                        lore(
                            listOf(
                                Component.text("클릭하여 수리 인벤토리를 엽니다.")
                                    .color(TextColor.color(Main.colors(ChatColor.AQUA).asRGB()))
                                    .decoration(TextDecoration.ITALIC,false)
                            )
                        )
                    }
                }
            ) {
                val pl = it.whoClicked as Player
                pl.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
                repair(pl).show(pl)
            }
            val chest = listOf(
                null, null, null, null, rp, null, null, null, null,
                p1, p2, p3, null, md, null, p4, p5, p6,
                u1, u2, u3, e1, e2, e3, l1, l2, l3,
            )
            val mainPane = StaticPane(0,0,9,3)
            var idx = 0
            var line = 0
            chest.forEach {
                if (idx == 9) {
                    idx = 0
                    line++
                }
                if (it != null) mainPane.addItem(it,idx,line)
                idx++
            }
            mineShop.addPane(mainPane)
            return mineShop
        }

        fun getMine1Cost(item : Material) : Int {
            return when (item) {
                Material.COBBLESTONE -> 500
                Material.COAL -> 3700
                else -> 0
            }
        }
        fun getMine2Cost(item : Material) : Int {
            return when (item) {
                Material.COBBLESTONE -> 300
                Material.COAL -> 1500
                Material.RAW_COPPER -> 1700
                Material.LAPIS_LAZULI -> 1200
                else -> 0
            }
        }
        fun getMine3Cost(item : Material) : Int {
            return when (item) {
                Material.COBBLESTONE -> 200
                Material.COAL -> 1000
                Material.RAW_COPPER -> 1100
                Material.LAPIS_LAZULI -> 600
                Material.RAW_IRON -> 1200
                Material.RAW_GOLD -> 1200
                Material.REDSTONE -> 100
                Material.DIAMOND -> 30000
                Material.EMERALD -> 75000
                else -> 0
            }
        }
        fun getMine4Cost(item : Material) : Int {
            return when (item) {
                Material.COBBLESTONE -> 200
                Material.COAL -> 1000
                Material.RAW_COPPER -> 1100
                Material.LAPIS_LAZULI -> 600
                Material.RAW_IRON -> 1200
                Material.RAW_GOLD -> 1200
                Material.REDSTONE -> 100
                Material.DIAMOND -> 30000
                Material.EMERALD -> 75000
                Material.ANCIENT_DEBRIS -> 350000
                else -> 0
            }
        }

        fun expWorks(e: BlockBreakEvent) {
            if (e.player.gameMode != GameMode.CREATIVE) {
                val p = e.player
                if (nonExpOre.contains(e.block.type)) {
                    if (e.block.isValidTool(p.inventory.itemInOffHand) || e.block.isValidTool(p.inventory.itemInMainHand)) {
                        when (e.block.type) {
                            Material.COPPER_ORE -> {
                                e.expToDrop = round(0.9 + Math.random() * (2.0 - 0.9)).toInt()
                            }
                            Material.IRON_ORE -> {
                                e.expToDrop = 1
                            }
                            Material.GOLD_ORE -> {
                                e.expToDrop = 1
                            }
                            Material.ANCIENT_DEBRIS -> {
                                e.expToDrop = round(5 + Math.random() * (10 - 5)).toInt()
                            }
                            else -> {}
                        }
                    }
                }
                if (e.player.hasPermission("miner.mine")) {
                    e.expToDrop = (e.expToDrop * Instance.config.getDouble("values.multiplier.miner")).toInt()
                }
                if (e.expToDrop > 0) {
                    Stats.setStat(e.expToDrop, p, "Mine")
                    Stats.showStat(p, "Mine")
                }
            }
        }
    }
}