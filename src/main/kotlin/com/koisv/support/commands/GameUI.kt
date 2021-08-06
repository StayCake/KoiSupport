package com.koisv.support.commands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.Main
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
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
            Bukkit.getScheduler().runTaskAsynchronously(
                Main.instance, Runnable {
                    inst.setOnClose {
                        Thread.currentThread().interrupt()
                    }
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER,1F, 1F))
                        tutorial.addItem(t1, 0, 0)
                        inst.update()
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER,1F, 1F))
                        tutorial.addItem(t2, 2, 0)
                        inst.update()
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER,1F, 1F))
                        tutorial.addItem(t3, 4, 0)
                        inst.update()
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        p.playSound(sound(Key.key("entity.item.pickup"), Source.PLAYER,1F, 1F))
                        tutorial.addItem(t4, 6, 0)
                        inst.update()
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))
                        tutorial.addItem(start, 8, 0)
                        inst.update()
                    })
                }
            )
        }
        fun game(p: Player, after: ((score: Int) -> Unit)? = null) {
            val ready = ChestGui(5, "§3준비")
            val countpane = StaticPane(9, 5)
            countpane.isVisible = true
            ready.addPane(countpane)
            ready.show(p)
            countpane.setOnClick { ck -> ck.isCancelled = true }
            val thgi = GuiItem(ItemStack(Material.GREEN_STAINED_GLASS_PANE))
            val twgi = GuiItem(ItemStack(Material.YELLOW_STAINED_GLASS_PANE))
            val ongi = GuiItem(ItemStack(Material.RED_STAINED_GLASS_PANE))
            val three = listOf(
                null, null, null, thgi, thgi, thgi, null, null, null,
                null, null, null, null, null, thgi, null, null, null,
                null, null, null, thgi, thgi, thgi, null, null, null,
                null, null, null, null, null, thgi, null, null, null,
                null, null, null, thgi, thgi, thgi, null, null, null,
            )
            val two = listOf(
                null, null, null, twgi, twgi, twgi, null, null, null,
                null, null, null, null, null, twgi, null, null, null,
                null, null, null, twgi, twgi, twgi, null, null, null,
                null, null, null, twgi, null, null, null, null, null,
                null, null, null, twgi, twgi, twgi, null, null, null,
            )
            val one = listOf(
                null, null, null, null, ongi, null, null, null, null,
                null, null, null, ongi, ongi, null, null, null, null,
                null, null, null, null, ongi, null, null, null, null,
                null, null, null, null, ongi, null, null, null, null,
                null, null, null, ongi, ongi, ongi, null, null, null,
            )

            Bukkit.getScheduler().runTaskAsynchronously(
                Main.instance, Runnable {
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        var x3 = 0
                        var y3 = 0
                        three.forEach { gi ->
                            if (x3 == 9) {
                                x3 = 0
                                y3++
                            }
                            if (gi != null) countpane.addItem(gi, x3, y3)
                            x3++
                        }
                        ready.update()
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER, 1F, 0.5F))
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        countpane.removeItem(thgi)
                        var x2 = 0
                        var y2 = 0
                        two.forEach { gi ->
                            if (x2 == 9) {
                                x2 = 0
                                y2++
                            }
                            if (gi != null) countpane.addItem(gi, x2, y2)
                            x2++
                        }
                        ready.update()
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.625F))
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        countpane.removeItem(twgi)
                        var x1 = 0
                        var y1 = 0
                        one.forEach { gi ->
                            if (x1 == 9) {
                                x1 = 0
                                y1++
                            }
                            if (gi != null) countpane.addItem(gi, x1, y1)
                            x1++
                        }
                        ready.update()
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.75F))
                    })
                    Thread.sleep(1000)
                    Bukkit.getScheduler().runTask(Main.instance, Runnable {
                        countpane.removeItem(ongi)
                        countpane.isVisible = false
                        ready.update()
                        p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
                        p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 1F))

                        val gameui = ChestGui(2,"미니게임")
                        val gamepane = StaticPane(9,2)
                        gameui.addPane(gamepane)
                        gameui.show(p)
                        var score = 0
                        val twoscoretap = GuiItem(
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
                            gamepane.removeItem(x,y)
                            gameui.update()
                        }
                        val twoscore = GuiItem(
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
                                gamepane.removeItem(x,y)
                                gameui.update()
                            } else {
                                it.isCancelled = true
                                p.playSound(sound(Key.key("entity.experience_orb.pickup"), Source.PLAYER,1F, 0.675F))
                                val y = it.slot/9
                                val x = it.slot - y*9
                                gamepane.addItem(twoscoretap,x,y)
                                gameui.update()
                            }
                        }
                        val onescore = GuiItem(
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
                            gamepane.removeItem(x,y)
                            gameui.update()
                        }
                        val minusscore = GuiItem(
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
                            gamepane.removeItem(x,y)
                            gameui.update()
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, Runnable{
                            gameui.setOnClose {
                                Bukkit.getScheduler().runTask(Main.instance, Runnable {
                                    if (it.reason != InventoryCloseEvent.Reason.CANT_USE) {
                                        p.closeInventory()
                                        p.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 0.5F))
                                        if (after == null) p.msg("$score 점") else after(score)
                                    }
                                })
                                Thread.currentThread().interrupt()
                            }
                            for (i in 1..60) {
                                Bukkit.getScheduler().runTask(Main.instance, Runnable {
                                    val ls = 61 - i
                                    if (ls <= 3) p.playSound(sound(Key.key("block.wooden_button.click_on"), Source.PLAYER,1F, 1F))
                                    gameui.title = "미니게임 | 남은시간 : ${if (ls <= 3) "&c" else ""}${ls}초"
                                })
                                val amount = (1 + Math.random() * (3 - 1)).roundToInt()
                                for (t in 1..amount) {
                                    val rd = (Math.random() * 10).roundToInt().toLong()
                                    val rx = (0 + Math.random() * (9 - 0)).roundToInt()
                                    val ry = (0 + Math.random() * (1 - 0)).roundToInt()
                                    when ((1 + Math.random() * (3 - 1)).roundToInt()) {
                                        1 -> {
                                            Bukkit.getScheduler().runTaskLater(
                                                Main.instance,
                                                Runnable {
                                                    gamepane.addItem(twoscore, rx, ry)
                                                    gameui.update()
                                                }, rd
                                            )
                                            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                                                gamepane.removeItem(rx,ry)
                                                gameui.update()
                                            },20L)
                                        }
                                        2 -> {
                                            Bukkit.getScheduler().runTaskLater(
                                                Main.instance,
                                                Runnable {
                                                    gamepane.addItem(onescore, rx, ry)
                                                    gameui.update()
                                                }, rd
                                            )
                                            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                                                gamepane.removeItem(rx,ry)
                                                gameui.update()
                                            },40L)
                                        }
                                        3 -> {
                                            Bukkit.getScheduler().runTaskLater(
                                                Main.instance,
                                                Runnable {
                                                    gamepane.addItem(minusscore, rx, ry)
                                                    gameui.update()
                                                }, rd
                                            )
                                            Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                                                gamepane.removeItem(rx,ry)
                                                gameui.update()
                                            },60L)
                                        }
                                    }
                                }
                                Thread.sleep(
                                    1000)
                            }
                            if (!Thread.currentThread().isInterrupted) Bukkit.getScheduler().runTask(Main.instance, Runnable {
                                p.closeInventory(InventoryCloseEvent.Reason.CANT_USE)
                                p.playSound(sound(Key.key("entity.player.levelup"), Source.PLAYER,1F, 1F))
                                if (after == null) p.msg("$score 점") else after(score)
                            })
                        })
                    })
                }
            )
        }
    }
}