package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.gui.type.GrindstoneGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.Main
import com.koisv.support.tools.Shops
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable

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

        val nonexpore = listOf(
            Material.COPPER_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.ANCIENT_DEBRIS
        )

        private fun repair(p: Player) : GrindstoneGui {
            val main = GrindstoneGui("도구 수리하기")
            main.setOnTopClick {
                if (it.slot == 1) {
                    if (it.inventory.contents[0] is Repairable) {
                        p.msg(it.inventory.contents[0].toString())
                        p.msg(it.inventory.contents[1].toString())
                        p.msg(it.inventory.contents[2].toString())
                    }
                }
            }
            return main
        }

        fun minegui(p: Player) : ChestGui {
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
            val p1 = Shops.shopItem(p,0,"부서진 곡괭이",50000,"\"아니, 캐지기는 하는 거야?\"","Mine",Material.WOODEN_PICKAXE)
            val p2 = Shops.shopItem(p,2,"닳은 곡괭이",30000,"가벼워서 부서질 수준이다.","Mine",Material.GOLDEN_PICKAXE)
            val p3 = Shops.shopItem(p,7,"중고 곡괭이",200000,"그나마 쓸 만한 녀석이다.","Mine",Material.STONE_PICKAXE)
            val p4 = Shops.shopItem(p,15,"곡괭이",750000,"철물점에서 본 듯한 녀석이다.","Mine",Material.IRON_PICKAXE)
            val p5 = Shops.shopItem(p,25,"전문 곡괭이",2000000,"관리되고 있는 녀석이라고 한다.","Mine",Material.DIAMOND_PICKAXE)
            val p6 = Shops.shopItem(p,40,"장인 곡괭이",5000000,"직접 갈고닦은 녀석이라고 한다.","Mine",Material.NETHERITE_PICKAXE)
            val md = Shops.shopItem(p, 35, Enchantment.MENDING, 1, "수선", 1000000, "이젠 무한의 시대.","Mine", pickaxe)
            val u1 = Shops.shopItem(p, 2, Enchantment.DURABILITY, 1, "내구성 I", 30000,"자그마한 납땜.","Mine", pickaxe)
            val u2 = Shops.shopItem(p, 6, Enchantment.DURABILITY, 2, "내구성 II", 70000,"철판 덧대기.","Mine", pickaxe)
            val u3 = Shops.shopItem(p, 18, Enchantment.DURABILITY, 3, "내구성 III", 150000,"망치질 추가하기.","Mine", pickaxe)
            val l1 = Shops.shopItem(p, 5, Enchantment.LOOT_BONUS_BLOCKS, 1, "행운 I", 85000,"네잎 클로버를 찾았다.","Mine", pickaxe)
            val l2 = Shops.shopItem(p, 12, Enchantment.LOOT_BONUS_BLOCKS, 2, "행운 II", 190000,"행운이 함께하길 빌었다.","Mine", pickaxe)
            val l3 = Shops.shopItem(p, 21, Enchantment.LOOT_BONUS_BLOCKS, 3, "행운 III", 320000,"신이 도와주길 바랬다.","Mine", pickaxe)
            val e1 = Shops.shopItem(p, 7, Enchantment.DIG_SPEED, 1, "효율 I",100000, "조금은 가벼워진 듯 하다.","Mine", pickaxe)
            val e2 = Shops.shopItem(p, 14, Enchantment.DIG_SPEED, 2, "효율 II",240000, "한 손으로 들만하다.","Mine", pickaxe)
            val e3 = Shops.shopItem(p, 26, Enchantment.DIG_SPEED, 3, "효율 III",380000, "종이 몇 장 수준이다.","Mine", pickaxe)
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
                Material.COBBLESTONE -> 300
                Material.COAL -> 2200
                Material.LAPIS_LAZULI -> 3600
                else -> 0
            }
        }
    }
}