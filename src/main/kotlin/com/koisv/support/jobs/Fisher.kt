package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.econ
import com.koisv.support.tools.Shops
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.concurrent.timerTask

class Fisher {
    companion object{
        val fishTreasure = ItemStack(Material.BRICK).apply {
            itemMeta = itemMeta.apply {
                lore(
                    listOf(
                        Component.text("무언가 오래된 듯 한 보물.")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.ORANGE.asRGB())),
                        Component.text("어부에게 그 가치를 물어보자.")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.YELLOW.asRGB()))
                    )
                )
                displayName(
                    Component.text("고대의 보물")
                        .decoration(TextDecoration.ITALIC,false)
                        .color(TextColor.color(Color.AQUA.asRGB()))
                )
            }
        }

        val fishTrash = ItemStack(Material.BOWL).apply {
            itemMeta = itemMeta.apply {
                lore(
                    listOf(
                        Component.text("누군가가 버린 듯 한 일회용 그릇.")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.SILVER.asRGB())),
                        Component.text("어부가 받을지는 모르겠다.")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.SILVER.asRGB()))
                    )
                )
                displayName(
                    Component.text("일회용 접시")
                        .decoration(TextDecoration.ITALIC,false)
                        .color(TextColor.color(Color.SILVER.asRGB()))
                )
            }
        }

        fun fishgui(p: Player) : ChestGui {
            val fishShop = ChestGui(3,"§e낚시품 상점")
            fishShop.setOnGlobalClick {
                if (it.isShiftClick) it.isCancelled = true
            }
            fishShop.setOnTopClick {
                it.isCancelled = true
            }
            fishShop.setOnGlobalDrag {
                it.isCancelled = true
            }
            val gmi = Shops.shopitem(p,0,"낚시대",50000,"낚시의 기본.","Fish", Material.FISHING_ROD)
            val sl1 = Shops.enchantshopitem(p,3, Enchantment.LUCK,1,"행운 I",10000,"보물을 낚고 싶은가?","Fish",listOf(Material.FISHING_ROD))
            val sl2 = Shops.enchantshopitem(p,5, Enchantment.LUCK,2,"행운 II",24000,"욕심이 생기지 않는가?","Fish",listOf(Material.FISHING_ROD))
            val sl3 = Shops.enchantshopitem(p,7, Enchantment.LUCK,3,"행운 III",50000,"운의 끝을 보고 싶은가?","Fish",listOf(Material.FISHING_ROD))
            val ub1 = Shops.enchantshopitem(p,8, Enchantment.DURABILITY,1,"내구성 I",20000,"단단함의 기초.","Fish",listOf(Material.FISHING_ROD))
            val ub2 = Shops.enchantshopitem(p,10, Enchantment.DURABILITY,2,"내구성 II",44000,"견고함의 중점.","Fish",listOf(Material.FISHING_ROD))
            val ub3 = Shops.enchantshopitem(p,13, Enchantment.DURABILITY,3,"내구성 III",96000,"전문가의 손길.","Fish",listOf(Material.FISHING_ROD))
            val bt1 = Shops.enchantshopitem(p,15, Enchantment.LURE,1,"미끼 I",22000,"숙련가의 가호가 느껴진다.","Fish",listOf(Material.FISHING_ROD))
            val bt2 = Shops.enchantshopitem(p,17, Enchantment.LURE,2,"미끼 II",54000,"전문가의 기운이 느껴진다.","Fish",listOf(Material.FISHING_ROD))
            val bt3 = Shops.enchantshopitem(p,20, Enchantment.LURE,3,"미끼 III",120000,"장인이 함께하는 듯 하다.","Fish",listOf(Material.FISHING_ROD))
            val md = Shops.enchantshopitem(p,30, Enchantment.MENDING,1,"수선",500000,"낚시의 정점.","Fish",listOf(Material.FISHING_ROD))
            val chest = listOf(
                null, null, sl1, null, ub1, null, bt1, null, null,
                gmi, null, sl2, null, ub2, null, bt2, null, md,
                null, null, sl3, null, ub3, null, bt3, null, null,
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
            fishShop.addPane(mainPane)
            return fishShop
        }

        fun fishTreasure(
            p: Player,
            amount: Int
        ) {
            p.inventory.setItemInMainHand(null)
            p.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.PLAYER, 1F, 1F))
            p.msg("§e어부 §7≫ §f아니 이건..?!")
            Timer().schedule(timerTask {
                p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                p.msg("§e어부 §7≫ §f말로만 듣던 고대의 보물..!")
                Timer().schedule(timerTask {
                    p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                    val scap = Math.random() * 100
                    val value: Int = when {
                        scap >= 50 -> {
                            ((20000 + Math.random() * (30000-20000)) * amount).toInt()
                        }
                        scap >= 4 -> {
                            ((30000 + Math.random() * (50000-30000)) * amount).toInt()
                        }
                        scap >= 1 -> {
                            ((50000 + Math.random() * (75000-50000)) * amount).toInt()
                        }
                        scap >= 0.999 -> {
                            ((750000 + Math.random() * (1200000-750000)) * amount).toInt()
                        }
                        else -> {
                            ((75000 + Math.random() * (125000-75000)) * amount).toInt()
                        }
                    }
                    p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                    p.msg("§e어부 §7≫ §f${value}원에 살게요!")
                    econ?.depositPlayer(p,value.toDouble())
                }, 2000L)
            }, 2000L)
        }

        fun fishTrash(
            p: Player,
            amount: Int
        ) {
            p.inventory.setItemInMainHand(null)
            p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER, 1F, 1F))
            p.msg("§e어부 §7≫ §f이걸 지금..")
            Timer().schedule(timerTask {
                p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                p.msg("§e어부 §7≫ §f받아달라는 건가요..?")
                Timer().schedule(timerTask {
                    p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                    val scap = Math.random() * 100
                    val value: Int = when {
                        scap >= 50 -> {
                            ((10 + Math.random() * (100-10)) * amount).toInt()
                        }
                        scap >= 4 -> {
                            ((100 + Math.random() * (250-100)) * amount).toInt()
                        }
                        scap >= 1 -> {
                            ((250 + Math.random() * (500-250)) * amount).toInt()
                        }
                        else -> {
                            ((500 + Math.random() * (1000-500)) * amount).toInt()
                        }
                    }
                    p.playSound(Sound.sound(Key.key("entity.item.pickup"), Sound.Source.PLAYER,1F, 1F))
                    p.msg("§e어부 §7≫ §f${value}원에 드리죠 뭐.")
                    econ?.depositPlayer(p,value.toDouble())
                }, 2000L)
            }, 2000L)
        }

        fun getFishCost(item : Material) : Int {
            return when (item) {
                Material.COD -> 200
                Material.SALMON -> 400
                Material.TROPICAL_FISH -> 900
                Material.PUFFERFISH -> 600
                else -> 0
            }
        }
    }
}