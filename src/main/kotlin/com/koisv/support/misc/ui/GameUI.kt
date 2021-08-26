package com.koisv.support.misc.ui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.Main
import com.koisv.support.Main.Companion.instance
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.schedule
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.roundToInt

class GameUI {
    companion object {
        fun execute(p: Player, next: (p: Player) -> Unit) {
            val inst = ChestGui(1, "§2미니게임!")
            val tutorial = StaticPane(9, 1)
            val t1 = GuiItem(
                ItemStack(Material.CYAN_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("30점")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Main.colors(ChatColor.AQUA).asRGB()))
                        )
                        lore(
                            listOf(
                                Component.text("더블클릭 필수!")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.DARK_AQUA).asRGB()))
                            )
                        )
                    }
                }
            ) { it.isCancelled = true }
            val t2 = GuiItem(
                ItemStack(Material.GREEN_STAINED_GLASS_PANE).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("10점")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Main.colors(ChatColor.GREEN).asRGB()))
                        )
                        lore(
                            listOf(
                                Component.text("0차")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.DARK_GREEN).asRGB()))
                            )
                        )
                    }
                }
            ) { it.isCancelled = true }
            val t3 = GuiItem(
                ItemStack(Material.TNT).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("-50점")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Main.colors(ChatColor.DARK_RED).asRGB()))
                        )
                        lore(
                            listOf(
                                Component.text("나-락")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.RED).asRGB()))
                            )
                        )
                    }
                }
            ) { it.isCancelled = true }
            val t4 = GuiItem(
                ItemStack(Material.PAPER).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("게임 진행 시간")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Main.colors(ChatColor.YELLOW).asRGB()))
                        )
                        lore(
                            listOf(
                                Component.text("60초")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.GOLD).asRGB()))
                            )
                        )
                    }
                }
            ) { it.isCancelled = true }
            val start = GuiItem(
                ItemStack(Material.ENCHANTED_GOLDEN_APPLE).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            Component.text("이걸 클릭해서")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Main.colors(ChatColor.WHITE).asRGB()))
                        )
                        lore(
                            listOf(
                                Component.text("게임 시작하기")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.LIGHT_PURPLE).asRGB())),
                                Component.text("창을 닫아 발생하는 피해는 전부 개인 책임입니다.")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Main.colors(ChatColor.RED).asRGB()))
                            )
                        )
                    }
                }
            ) {
                it.isCancelled = true
                tutorial.isVisible = false
                p.closeInventory()
                next(p)
            }
            inst.addPane(tutorial)
            inst.show(p)
            instance.schedule(true) {
                if (inst.viewers.contains(p)) Thread.sleep(1000)
                if (inst.viewers.contains(p)) instance.schedule(false) {
                    p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER, 1F, 1F))
                    tutorial.addItem(t1, 0, 0)
                    inst.update()
                }
                if (inst.viewers.contains(p)) Thread.sleep(1000)
                if (inst.viewers.contains(p)) instance.schedule(false) {
                    p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER, 1F, 1F))
                    tutorial.addItem(t2, 2, 0)
                    inst.update()
                }
                if (inst.viewers.contains(p)) Thread.sleep(1000)
                if (inst.viewers.contains(p)) instance.schedule(false) {
                    p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER, 1F, 1F))
                    tutorial.addItem(t3, 4, 0)
                    inst.update()
                }
                if (inst.viewers.contains(p)) Thread.sleep(1000)
                if (inst.viewers.contains(p)) instance.schedule(false) {
                    p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER, 1F, 1F))
                    tutorial.addItem(t4, 6, 0)
                    inst.update()
                }
                if (inst.viewers.contains(p)) Thread.sleep(1000)
                if (inst.viewers.contains(p)) instance.schedule(false) {
                    p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))
                    tutorial.addItem(start, 8, 0)
                    inst.update()
                }
            }
        }
        fun game(p: Player ,after: ((score: Int) -> Unit)? = null) {
            p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
            val ready = ChestGui(5, "§3준비")
            val countPane = StaticPane(9, 5)
            countPane.isVisible = true
            ready.addPane(countPane)
            ready.show(p)
            countPane.setOnClick { ck -> ck.isCancelled = true }
            val thGi = GuiItem(ItemStack(Material.GREEN_STAINED_GLASS_PANE))
            val twGi = GuiItem(ItemStack(Material.YELLOW_STAINED_GLASS_PANE))
            val onGi = GuiItem(ItemStack(Material.RED_STAINED_GLASS_PANE))
            val three = listOf(
                null, null, null, thGi, thGi, thGi, null, null, null,
                null, null, null, null, null, thGi, null, null, null,
                null, null, null, thGi, thGi, thGi, null, null, null,
                null, null, null, null, null, thGi, null, null, null,
                null, null, null, thGi, thGi, thGi, null, null, null,
            )
            val two = listOf(
                null, null, null, twGi, twGi, twGi, null, null, null,
                null, null, null, null, null, twGi, null, null, null,
                null, null, null, twGi, twGi, twGi, null, null, null,
                null, null, null, twGi, null, null, null, null, null,
                null, null, null, twGi, twGi, twGi, null, null, null,
            )
            val one = listOf(
                null, null, null, null, onGi, null, null, null, null,
                null, null, null, onGi, onGi, null, null, null, null,
                null, null, null, null, onGi, null, null, null, null,
                null, null, null, null, onGi, null, null, null, null,
                null, null, null, onGi, onGi, onGi, null, null, null,
            )
            instance.schedule(true) {
                if (ready.viewers.contains(p)) instance.schedule(false) {
                    var x3 = 0
                    var y3 = 0
                    three.forEach { gi ->
                        if (x3 == 9) {
                            x3 = 0
                            y3++
                        }
                        if (gi != null) countPane.addItem(gi, x3, y3)
                        x3++
                    }
                    ready.update()
                    p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER, 1F, 0.5F))
                }
                if (ready.viewers.contains(p)) Thread.sleep(1000)
                if (ready.viewers.contains(p)) instance.schedule(false)  {
                    countPane.removeItem(thGi)
                    var x2 = 0
                    var y2 = 0
                    two.forEach { gi ->
                        if (x2 == 9) {
                            x2 = 0
                            y2++
                        }
                        if (gi != null) countPane.addItem(gi, x2, y2)
                        x2++
                    }
                    ready.update()
                    p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.625F))
                }
                if (ready.viewers.contains(p)) Thread.sleep(1000)
                if (ready.viewers.contains(p)) instance.schedule(false)  {
                    countPane.removeItem(twGi)
                    var x1 = 0
                    var y1 = 0
                    one.forEach { gi ->
                        if (x1 == 9) {
                            x1 = 0
                            y1++
                        }
                        if (gi != null) countPane.addItem(gi, x1, y1)
                        x1++
                    }
                    ready.update()
                    p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.75F))
                }
                if (ready.viewers.contains(p)) Thread.sleep(1000)
                if (ready.viewers.contains(p)) instance.schedule(false)  {
                    countPane.removeItem(onGi)
                    countPane.isVisible = false
                    ready.update()
                    p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
                    p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))

                    val gameUI = ChestGui(2,"미니게임")
                    val gamePane = StaticPane(9,2)
                    gameUI.addPane(gamePane)
                    gameUI.show(p)
                    var score = 0
                    val twoScoreTap = GuiItem(
                        ItemStack(Material.CYAN_STAINED_GLASS_PANE,1).apply {
                            itemMeta = itemMeta.apply {
                                displayName(
                                    Component.text("30점")
                                        .decoration(TextDecoration.ITALIC,false)
                                        .color(TextColor.color(Main.colors(ChatColor.AQUA).asRGB()))
                                )
                                lore(
                                    listOf(
                                        Component.text("한번 더!")
                                            .decoration(TextDecoration.ITALIC,false)
                                            .decorate(TextDecoration.UNDERLINED)
                                            .decorate(TextDecoration.BOLD)
                                            .color(TextColor.color(Main.colors(ChatColor.DARK_AQUA).asRGB()))
                                    )
                                )
                            }
                        }
                    ) {
                        it.isCancelled = true
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))
                        score += 30
                        val y = it.slot/9
                        val x = it.slot - y*9
                        gamePane.removeItem(x,y)
                        gameUI.update()
                    }
                    val twoScore = GuiItem(
                        ItemStack(Material.CYAN_STAINED_GLASS_PANE,2).apply {
                            itemMeta = itemMeta.apply {
                                displayName(
                                    Component.text("30점")
                                        .decoration(TextDecoration.ITALIC,false)
                                        .color(TextColor.color(Main.colors(ChatColor.AQUA).asRGB()))
                                )
                                lore(
                                    listOf(
                                        Component.text("더블클릭 필수!")
                                            .decoration(TextDecoration.ITALIC,false)
                                            .color(TextColor.color(Main.colors(ChatColor.DARK_AQUA).asRGB()))
                                    )
                                )
                            }
                        }
                    ) {
                        if (it.click == ClickType.DOUBLE_CLICK) {
                            it.isCancelled = true
                            p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))
                            score += 30
                            val y = it.slot/9
                            val x = it.slot - y*9
                            gamePane.removeItem(x,y)
                            gameUI.update()
                        } else {
                            it.isCancelled = true
                            p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.675F))
                            val y = it.slot/9
                            val x = it.slot - y*9
                            gamePane.addItem(twoScoreTap,x,y)
                            gameUI.update()
                        }
                    }
                    val oneScore = GuiItem(
                        ItemStack(Material.LIME_STAINED_GLASS_PANE).apply {
                            itemMeta = itemMeta.apply {
                                displayName(
                                    Component.text("10점")
                                        .decoration(TextDecoration.ITALIC,false)
                                        .color(TextColor.color(Main.colors(ChatColor.GREEN).asRGB()))
                                )
                            }
                        }
                    ) {
                        it.isCancelled = true
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,0.5F, 0.5F))
                        score += 10
                        val y = it.slot/9
                        val x = it.slot - y*9
                        gamePane.removeItem(x,y)
                        gameUI.update()
                    }
                    val minusScore = GuiItem(
                        ItemStack(Material.TNT).apply {
                            itemMeta = itemMeta.apply {
                                displayName(
                                    Component.text("-50점")
                                        .decoration(TextDecoration.ITALIC,false)
                                        .color(TextColor.color(Main.colors(ChatColor.DARK_RED).asRGB()))
                                )
                            }
                        }
                    ) {
                        it.isCancelled = true
                        p.playSound(sound(Key.key("entity.generic.explode"), Source.PLAYER,0.5F, 1F))
                        score -= 50
                        val y = it.slot/9
                        val x = it.slot - y*9
                        gamePane.removeItem(x,y)
                        gameUI.update()
                    }
                    instance.schedule(true) {
                        var alive = true
                        gameUI.setOnClose {
                            instance.schedule(false) {
                                if (it.reason != InventoryCloseEvent.Reason.CANT_USE) {
                                    p.closeInventory()
                                    p.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 0.5F))
                                    if (after == null) p.msg("$score 점") else after(score)
                                    alive = false
                                }
                            }
                        }
                        for (i in 1..60) {
                            if (alive) {
                                println(alive)
                                instance.schedule(false) {
                                    val ls = 61 - i
                                    if (ls <= 3) p.playSound(
                                        sound(
                                            Key.key("block.wooden_button.click_on"),
                                            Source.PLAYER,
                                            1F,
                                            1F
                                        )
                                    )
                                    gameUI.title = "미니게임 | $score 점 | 남은시간 : ${if (ls <= 3) "§c" else ""}${ls}초"
                                }
                                val amount = (1 + Math.random() * (3 - 1)).roundToInt()
                                for (t in 1..amount) {
                                    val rd = (Math.random() * 10).roundToInt().toLong()
                                    val rx = (0 + Math.random() * (9 - 0)).roundToInt()
                                    val ry = (0 + Math.random() * (1 - 0)).roundToInt()
                                    when ((1 + Math.random() * (3 - 1)).roundToInt()) {
                                        1 -> {
                                            instance.schedule(false, rd / 20) {
                                                gamePane.addItem(twoScore, rx, ry)
                                                gameUI.update()
                                            }
                                            instance.schedule(false, 1) {
                                                gamePane.removeItem(rx, ry)
                                                gameUI.update()
                                            }
                                        }
                                        2 -> {
                                            instance.schedule(false, rd / 20) {
                                                gamePane.addItem(oneScore, rx, ry)
                                                gameUI.update()
                                            }
                                            instance.schedule(false, 2) {
                                                gamePane.removeItem(rx, ry)
                                                gameUI.update()
                                            }
                                        }
                                        3 -> {
                                            instance.schedule(false, rd / 20) {
                                                gamePane.addItem(minusScore, rx, ry)
                                                gameUI.update()
                                            }
                                            instance.schedule(false, 3) {
                                                gamePane.removeItem(rx, ry)
                                                gameUI.update()
                                            }
                                        }
                                    }
                                }
                                Thread.sleep(1000)
                                if (i == 60) alive = false
                            }
                        }
                        if (gameUI.viewers.contains(p)) instance.schedule(false) {
                            p.closeInventory(InventoryCloseEvent.Reason.CANT_USE)
                            p.playSound(sound(Key.key("entity.player.levelup"), Source.PLAYER,1F, 1F))
                            if (after == null) p.msg("$score 점") else after(score)
                        }
                    }
                }
            }
        }
    }
}