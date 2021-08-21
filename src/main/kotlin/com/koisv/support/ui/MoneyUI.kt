package com.koisv.support.ui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.Orientable
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.component.Slider
import com.koisv.support.chat
import com.koisv.support.econ
import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandSource
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import kotlin.math.ceil


class MoneyUI {
    companion object {
        fun mainui(it: KommandSource, prv: StaticPane? = null): ChestGui {
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
                                    Component.text((chat?.getPlayerPrefix(p) as String).split("[")[1].split("]")[0]).decoration(
                                        TextDecoration.ITALIC, false)
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
                paytarget(e.whoClicked as Player) {
                    mainui(it,moneyp).show(e.whoClicked)
                }
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
            moneyp.addItem(check,3,1)

            g.setOnClose {
                if (it.reason != InventoryCloseEvent.Reason.OPEN_NEW) {
                    when {
                        prv?.isVisible == true -> {
                            prv.isVisible = false
                            mainp.isVisible = true
                            g.show(it.player)
                            g.update()
                        }
                        moneyp.isVisible -> {
                            moneyp.isVisible = false
                            mainp.isVisible = true
                            g.show(it.player)
                            g.update()
                        }
                    }
                }
            }

            g.addPane(moneyp)
            g.addPane(mainp)
            g.panes.forEach { it.isVisible = false }
            if (prv != null) {
                g.addPane(prv)
                prv.isVisible = true
            } else {
                mainp.isVisible = true
            }
            return g
        }
        private fun paytarget(p: Player, callback: () -> Unit) {
            p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
            val players = Bukkit.getOnlinePlayers()
            val length = ceil(players.size.toFloat() / 9)
            val paypane = OutlinePane(0,0,if (length > 6) 8 else 9,length.toInt())
            val payselect = ChestGui(if (length > 6) 6 else length.toInt(),"대상 선택")
            paypane.orientation = Orientable.Orientation.HORIZONTAL // 수평 -
            if (paypane.length == 8) {
                val slider = Slider(8, 0, 1, 6)
                val pages = ceil(players.size.toFloat() / 48)
                val terms = 1.0 / pages
                slider.setOnClick {
                    val value = slider.value
                    var term = 0.0
                    var line = 0
                    while (term <= 1) {
                        if (value < term) {
                            paypane.x = 0-line
                            payselect.update()
                        }
                        term += terms
                        line++
                    }
                }
                slider.orientation = Orientable.Orientation.VERTICAL // 수직 |
                payselect.addPane(slider)
            }
            players.forEach { it2 ->
                if (it2 == p) return@forEach
                paypane.addItem(makeskull(it2) {
                    amount(p) { it3, amount ->
                        val pl = it3.whoClicked
                        pl.closeInventory(InventoryCloseEvent.Reason.UNLOADED)
                        econ?.withdrawPlayer(it3.whoClicked as Player,amount)
                        econ?.depositPlayer(it2,amount)
                        pl.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 2F))
                        it3.whoClicked.msg("${it2.name}님에게 ${amount.toInt()}원을 보냈습니다.")
                        it2.msg("${pl.name}님이 당신에게 ${amount.toInt()}원을 보냈습니다.")
                    }
                })
            }
            paypane.isVisible = true
            payselect.setOnClose {
                if (it.reason != InventoryCloseEvent.Reason.OPEN_NEW) callback()
            }
            payselect.addPane(paypane)
            payselect.show(p)
        }
        private fun makeskull(p: Player,event:(ice: InventoryClickEvent) -> Unit) : GuiItem{
            return GuiItem(
                ItemStack(Material.PLAYER_HEAD).apply {
                    itemMeta = itemMeta.apply {
                        val skull = this as SkullMeta
                        skull.owningPlayer = p
                        displayName(p.displayName().decoration(TextDecoration.ITALIC,false).append(Component.text("님에게 보내기")))
                    }
                }
            ){
                event(it)
            }
        }
        private fun amount(p:Player, nextwork: (event: InventoryClickEvent,result: Double) -> Unit) {
            var index = 0
            var amount = 0.0
            var valst = "1"
            val amountGui = ChestGui(3,"금액 결정")
            val amountpane = StaticPane(9,3)
            val amountpaper = ItemStack(Material.PAPER).apply {
                itemMeta = itemMeta.apply {
                    displayName(Component.text("${amount.toInt()} 원")
                        .decoration(TextDecoration.ITALIC,false))
                    lore(listOf(
                        Component.text("좌클릭 : 결정")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.LIME.asRGB())),
                        Component.text("우클릭 : 초기화")
                            .decoration(TextDecoration.ITALIC,false)
                            .color(TextColor.color(Color.RED.asRGB()))
                    ))
                }
            }
            fun paperevent(it: InventoryClickEvent) {
                it.isCancelled = true
                when {
                    it.isLeftClick -> {
                        if (amount == 0.0) {
                            it.whoClicked.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 0.5F))
                            it.whoClicked.msg("0이 될 수 없습니다.")
                        } else nextwork(it,amount)
                    }
                    it.isRightClick -> {
                        amount = 0.0
                        amountpane.addItem(GuiItem(amountpaper.apply {
                            itemMeta = itemMeta.apply {
                                displayName(Component.text("금액 : ${amount.toInt()} 원")
                                    .decoration(TextDecoration.ITALIC,false))
                            }
                        }) { pp -> paperevent(pp)},4,2)
                        amountGui.update()
                    }
                }
            }
            while (index < 9) {
                val value = valst.toInt()
                amountpane.addItem(
                    GuiItem(
                        ItemStack(Material.YELLOW_STAINED_GLASS_PANE).apply {
                            itemMeta = itemMeta.apply {
                                displayName(Component.text("금액 : $valst 원")
                                    .decoration(TextDecoration.ITALIC,false))
                                lore(
                                    listOf(
                                        Component.text("좌클릭 : +$valst")
                                            .decoration(TextDecoration.ITALIC,false)
                                            .color(TextColor.color(Color.LIME.asRGB())),
                                        Component.text("우클릭 : -$valst")
                                            .decoration(TextDecoration.ITALIC,false)
                                            .color(TextColor.color(Color.RED.asRGB()))
                                    )
                                )
                            }
                        }
                    ) {
                        it.isCancelled = true
                        val pice = it.whoClicked as Player
                        when {
                            it.isLeftClick -> {
                                if (econ?.has(pice,amount + value) == true) {
                                    amount += value
                                    amountpane.addItem(GuiItem(amountpaper.apply {
                                        itemMeta = itemMeta.apply {
                                            displayName(Component.text("금액 : ${amount.toInt()} 원")
                                                .decoration(TextDecoration.ITALIC,false))
                                        }
                                    }) { pp -> paperevent(pp)},4,2)
                                    amountGui.update()
                                } else {
                                    pice.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 0.5F))
                                    pice.msg("소지금이 부족합니다.")
                                }
                            }
                            it.isRightClick -> {
                                if (amount - value >= 0) {
                                    amount -= value
                                    amountpane.addItem(GuiItem(amountpaper.apply {
                                        itemMeta = itemMeta.apply {
                                            displayName(Component.text("금액 : ${amount.toInt()} 원")
                                                .decoration(TextDecoration.ITALIC,false))
                                        }
                                    }) { pm -> paperevent(pm)},4,2)
                                    amountGui.update()
                                } else {
                                    pice.playSound(sound(Key.key("block.note_block.harp"), Source.PLAYER,1F, 0.5F))
                                    pice.msg("음수가 될 수 없습니다.")
                                }
                            }
                        }
                    },index,1
                )
                index++
                valst += "0"
            }
            amountpane.addItem(GuiItem(amountpaper) { fp -> paperevent(fp)},4,2)
            amountGui.addPane(amountpane)
            p.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
            amountGui.show(p)
        }
    }
}