package com.koisv.support.commands

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.chat
import com.koisv.support.econ
import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandSource
import io.github.monun.kommand.node.LiteralNode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object Menu {

    private fun mainui(it: KommandSource): ChestGui {
        val moneyp = StaticPane(9,3)
        val p = it.sender as Player
        val g = ChestGui(3, "§a메뉴")
        val mainp = StaticPane(9,4)
        val mainh = ItemStack(Material.PLAYER_HEAD).apply {
            itemMeta = itemMeta.apply {
                val skull: SkullMeta = this as SkullMeta
                skull.owningPlayer = p
                lore(
                    listOf(
                        Component.text("사용자명 : ")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(255, 200, 100))
                            .append(
                                Component.text(p.name)
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(255, 200, 100))
                            ),
                        Component.text("직급 : ")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(255, 200, 100))
                            .append(
                                Component.text((chat?.getPlayerPrefix(p) as String).split("[")[1].split("]")[0]).decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(255, 200, 100))
                            )
                    )
                )
                displayName(
                    Component.text("[ 정보 ]")
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(105, 190, 190))
                )
            }
        }
        val btn1i = ItemStack(Material.DIAMOND).apply {
            itemMeta = itemMeta.apply {
                lore(
                    listOf(
                        Component.text("소지금 관련 메뉴입니다.")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(255, 200, 100)),
                        Component.text("클릭하여 소지금 메뉴를 엽니다.")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(255, 200, 100))
                    )
                )
                displayName(
                    Component.text(
                        "소지금 : ${econ?.getBalance(p)?.toInt()}원"
                    )
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(225, 225, 50))
                )
            }
        }
        val btn1 = GuiItem(btn1i) { e ->
            e.isCancelled = true
            mainp.isVisible = false
            moneyp.isVisible = true
            g.update()
        }
        val head = GuiItem(mainh)
        mainp.addItem(head, 4, 0)
        mainp.addItem(btn1, 1, 1)
        g.setOnGlobalClick { e ->
            e.isCancelled = true
        }
        g.addPane(mainp)


        val pay = GuiItem(
            ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA).apply {
                itemMeta = itemMeta.apply {
                    displayName(
                        Component.text("보내기")
                            .decoration(TextDecoration.ITALIC,false)
                    )
                }
            }
        ) { e ->
            e.isCancelled = true
            val current = Bukkit.getOnlinePlayers()
            current.forEach {
                val p2 = it as Player
                val index = current.indexOf(p2)
            }
            e.whoClicked.msg("미완성 된 기능입니다.")
            moneyp.isVisible = false
            mainp.isVisible = true
            g.update()
        }
        val check = GuiItem(
            ItemStack(Material.PAPER).apply {
                itemMeta = itemMeta.apply {
                    displayName(
                        Component.text("수표 출력하기")
                            .decoration(TextDecoration.ITALIC,false)
                    )
                }
            }
        )
        moneyp.addItem(pay,1,1)
        moneyp.isVisible = false
        g.addPane(moneyp)


        g.setOnClose {
            when {
                moneyp.isVisible -> {
                    moneyp.isVisible = false
                    mainp.isVisible = true
                    g.show(it.player)
                    g.update()
                }
            }
        }
        return g
    }

    fun register(builder: LiteralNode) {
        builder.requires { playerOrNull != null }
        builder.executes {
            mainui(this).show(sender as Player)
        }
    }
}