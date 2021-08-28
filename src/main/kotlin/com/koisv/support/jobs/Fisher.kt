package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.econ
import com.koisv.support.misc.tools.Instance
import com.koisv.support.misc.tools.Shops
import com.koisv.support.misc.tools.Stats.setStat
import com.koisv.support.misc.tools.Stats.showStat
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.textOf
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.round

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

        fun fishGui(p: Player) : ChestGui {
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
            val gmi = Shops.shopItem(p,0,"낚시대",50000,"낚시의 기본.","Fish", Material.FISHING_ROD)
            val sl1 = Shops.shopItem(p,3, Enchantment.LUCK,1,"행운 I",10000,"보물을 낚고 싶은가?","Fish",listOf(Material.FISHING_ROD))
            val sl2 = Shops.shopItem(p,5, Enchantment.LUCK,2,"행운 II",24000,"욕심이 생기지 않는가?","Fish",listOf(Material.FISHING_ROD))
            val sl3 = Shops.shopItem(p,7, Enchantment.LUCK,3,"행운 III",50000,"운의 끝을 보고 싶은가?","Fish",listOf(Material.FISHING_ROD))
            val ub1 = Shops.shopItem(p,8, Enchantment.DURABILITY,1,"내구성 I",20000,"단단함의 기초.","Fish",listOf(Material.FISHING_ROD))
            val ub2 = Shops.shopItem(p,10, Enchantment.DURABILITY,2,"내구성 II",44000,"견고함의 중점.","Fish",listOf(Material.FISHING_ROD))
            val ub3 = Shops.shopItem(p,13, Enchantment.DURABILITY,3,"내구성 III",96000,"전문가의 손길.","Fish",listOf(Material.FISHING_ROD))
            val bt1 = Shops.shopItem(p,15, Enchantment.LURE,1,"미끼 I",22000,"숙련가의 가호가 느껴진다.","Fish",listOf(Material.FISHING_ROD))
            val bt2 = Shops.shopItem(p,17, Enchantment.LURE,2,"미끼 II",54000,"전문가의 기운이 느껴진다.","Fish",listOf(Material.FISHING_ROD))
            val bt3 = Shops.shopItem(p,20, Enchantment.LURE,3,"미끼 III",120000,"장인이 함께하는 듯 하다.","Fish",listOf(Material.FISHING_ROD))
            val md = Shops.shopItem(p,30, Enchantment.MENDING,1,"수선",500000,"낚시의 정점.","Fish",listOf(Material.FISHING_ROD))
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
                    val sCap = Math.random() * 100
                    val value: Int = when {
                        sCap >= 50 -> {
                            ((20000 + Math.random() * (30000-20000)) * amount).toInt()
                        }
                        sCap >= 4 -> {
                            ((30000 + Math.random() * (50000-30000)) * amount).toInt()
                        }
                        sCap >= 1 -> {
                            ((50000 + Math.random() * (75000-50000)) * amount).toInt()
                        }
                        sCap >= 0.999 -> {
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
                    val sCap = Math.random() * 100
                    val value: Int = when {
                        sCap >= 50 -> {
                            ((10 + Math.random() * (100-10)) * amount).toInt()
                        }
                        sCap >= 4 -> {
                            ((100 + Math.random() * (250-100)) * amount).toInt()
                        }
                        sCap >= 1 -> {
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

        fun jobWorks(e: PlayerFishEvent) {
            if (e.state == PlayerFishEvent.State.CAUGHT_FISH) {
                val item = e.caught as Item
                val lp = Math.random() * 100
                when {
                    lp >= 50 -> {
                        val vp = (Math.random() * 100).toInt()
                        val coin: Int = when {
                            vp >= 60 -> {
                                100
                            }
                            vp >= 30 -> {
                                500
                            }
                            vp >= 10 -> {
                                50
                            }
                            else -> {
                                10
                            }
                        }
                        econ?.depositPlayer(e.player, coin.toDouble())
                        e.player.playSound(
                            Sound.sound(
                                Key.key("entity.experience_orb.pickup"),
                                Sound.Source.PLAYER,
                                1F,
                                1F
                            )
                        )
                        e.player.sendActionBar(
                            Component.text().content(
                                "${coin}원 짜리가 같이 걸렸습니다."
                            ).build()
                        )
                    }
                    lp >= 49 -> {
                        val vp = (Math.random() * 100).toInt()
                        val coin: Int = when {
                            vp <= 75 -> {
                                1
                            }
                            else -> {
                                5
                            }
                        }
                        econ?.depositPlayer(e.player, (coin * 10000).toDouble())
                        e.player.playSound(
                            Sound.sound(
                                Key.key("entity.player.levelup"),
                                Sound.Source.PLAYER,
                                1F,
                                1F
                            )
                        )
                        e.player.sendActionBar(
                            Component.text().content(
                                "누군가 ${coin}만원 짜리 지폐를 흘린 듯 합니다."
                            ).build()
                        )

                    }
                    lp >= 48.99 -> {
                        val culture = ItemStack(Material.PAPER)
                        val tl = listOf(
                            Component.text("농담 안하고 진짜 바꿔준다.")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(255, 0, 0)),
                            Component.text("이걸 낚은 당신에게 감사하자.")
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        val tm = culture.itemMeta
                        tm.displayName(
                            Component.text("문화상품권 5천원")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(255, 255, 0))
                        )
                        tm.lore(tl)
                        culture.itemMeta = tm
                        item.itemStack = culture
                        e.player.playSound(
                            Sound.sound(
                                Key.key("ui.toast.challenge_complete"),
                                Sound.Source.PLAYER,
                                1F,
                                0.7F
                            )
                        )
                        e.player.msg(
                            textOf("엄청난 일이 일어난 듯 합니다...?")
                        )
                    }
                    else -> {
                    }
                }
                val got = item.itemStack
                if (
                    got == ItemStack(Material.PUFFERFISH)
                    || got == ItemStack(Material.COD)
                    || got == ItemStack(Material.SALMON)
                    || got == ItemStack(Material.TROPICAL_FISH)
                ){
                    if (
                        got == ItemStack(Material.COD)
                        || item.itemStack == ItemStack(Material.SALMON)
                    )
                    {
                        val sCap = Math.random() * 100
                        val length: Float = when {
                            sCap >= 46 -> {
                                (round((0.01 + Math.random() * (49.99 - 0.01))) * 100 / 100).toFloat()
                            }
                            sCap >= 26 -> {
                                (round((50 + Math.random() * (99.99 - 50))) * 100 / 100).toFloat()
                            }
                            sCap >= 11 -> {
                                (round((100 + Math.random() * (149.99 - 100))) * 100 / 100).toFloat()
                            }
                            sCap >= 6 -> {
                                (round((150 + Math.random() * (199.99 - 150))) * 100 / 100).toFloat()
                            }
                            sCap >= 3 -> {
                                (round((200 + Math.random() * (209.99 - 200))) * 100 / 100).toFloat()
                            }
                            sCap >= 1 -> {
                                (round((210 + Math.random() * (229.99 - 210))) * 100 / 100).toFloat()
                            }
                            else -> {
                                (round((230 + Math.random() * (250 - 230))) * 100 / 100).toFloat()
                            }
                        }
                        if (length >= 200) {
                            Bukkit.getOnlinePlayers().forEach {
                                it.sendMessage(
                                    Component.text("시스템")
                                        .color(TextColor.color(Color.LIME.asRGB()))
                                        .append(
                                            Component.text(" >> ")
                                                .color(TextColor.color(Color.GRAY.asRGB()))
                                        )
                                        .append(
                                            e.player.displayName()
                                                .color(TextColor.color(Color.AQUA.asRGB()))
                                        )
                                        .append(
                                            Component.text("님이 ")
                                                .color(TextColor.color(Color.WHITE.asRGB()))

                                        ).append(
                                            Component.text("${length}cm")
                                                .color(TextColor.color(Color.YELLOW.asRGB()))
                                        ).append(
                                            Component.text(" 길이의 대어를 낚았습니다!")
                                                .color(TextColor.color(Color.WHITE.asRGB()))
                                        )
                                )
                            }
                            e.player.playSound(
                                Sound.sound(
                                    Key.key("entity.player.levelup"),
                                    Sound.Source.PLAYER,
                                    1F,
                                    0.5F
                                )
                            )
                        }
                        val ca = item.itemStack
                        val cm = ca.itemMeta
                        val cl = listOf(
                            Component.text("${length}cm")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(255, 255, 255))
                        )
                        cm.lore(cl)
                        ca.itemMeta = cm
                        item.itemStack = ca
                    }
                    if (e.player.hasPermission("fisher.fish")) {
                        e.expToDrop = (e.expToDrop * Instance.config.getDouble("values.multiplier.fisher")).toInt()
                        e.player.setStat(e.expToDrop, "Fish")
                    } else {
                        e.player.setStat(e.expToDrop, "Fish")
                    }
                } else {
                    val mh = e.player.inventory.itemInMainHand
                    val oh = e.player.inventory.itemInOffHand
                    var mhe : Int? = 0
                    var ohe : Int? = 0
                    when (Material.FISHING_ROD){
                        mh.type  -> mhe = mh.getEnchantmentLevel(Enchantment.LUCK)
                        oh.type -> ohe = oh.getEnchantmentLevel(Enchantment.LUCK)
                        else -> {
                            mhe = 0
                            ohe = 0
                        }
                    }
                    val bl: Int? = when {
                        mh.type == Material.FISHING_ROD || oh.type != Material.FISHING_ROD -> mhe
                        mh.type != Material.FISHING_ROD || oh.type != Material.FISHING_ROD -> ohe
                        mh.type == oh.type || mh.type == Material.FISHING_ROD -> mhe
                        else -> 0
                    }
                    when (bl) {
                        0 -> {
                            val lrv = round(1 + Math.random() * (15 - 1))
                            if (lrv <= 5) {
                                item.itemStack = fishTreasure
                            }
                            else {
                                item.itemStack = fishTrash
                            }
                        }
                        1 -> {
                            val lrv = round(1 + Math.random() * (152 - 1))
                            if (lrv <= 71) {
                                item.itemStack = fishTreasure
                            }
                            else {
                                item.itemStack = fishTrash
                            }
                        }
                        2 -> {
                            val lrv = round(1 + Math.random() * (153 - 1))
                            if (lrv <= 92) {
                                item.itemStack = fishTreasure
                            }
                            else {
                                item.itemStack = fishTrash
                            }
                        }
                        3 -> {
                            val lrv = round(1 + Math.random() * (155 - 1))
                            if (lrv <= 113) {
                                item.itemStack = fishTreasure
                            }
                            else {
                                item.itemStack = fishTrash
                            }
                        }
                    }
                }
                e.player.showStat("Fish")
            }
            if (e.state == PlayerFishEvent.State.FAILED_ATTEMPT || e.state == PlayerFishEvent.State.REEL_IN || e.state == PlayerFishEvent.State.IN_GROUND) {
                e.player.sendActionBar(
                    Component.text().content("아무것도 낚지 못한 듯 합니다...").build()
                )
            }
        }
    }
}