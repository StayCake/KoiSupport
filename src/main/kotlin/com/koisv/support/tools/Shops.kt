package com.koisv.support.tools

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.koisv.support.econ
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class Shops {
    companion object {
        private fun buymessage(
            target: Player,
            price: Int,
            item: ItemStack,
            who: String,
        ) {
            val money : Boolean = econ?.getBalance(target)!! >= price
            val space : Boolean = target.inventory.firstEmpty() != -1
            if (money && space) {
                econ?.withdrawPlayer(target, price.toDouble())
                target.inventory.addItem(item.apply {
                    itemMeta = itemMeta.apply { lore(listOf(lore()?.get(0))) }
                })
                target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
            } else if (money && !space) {
                target.msg("$who §7≫ §f가방에 들어갈 공간이 없는 듯 하네요.")
            } else {
                target.msg("$who §7≫ §f돈이 부족하네요.")
            }
        }

        private fun enchantmessage(
            target: Player,
            price: Int,
            item: ItemStack,
            who: String,
            enchantfrom: ItemStack
        ) {
            val money : Boolean = econ?.getBalance(target)!! >= price
            if (money) {
                econ?.withdrawPlayer(target, price.toDouble())
                enchantfrom.enchantments.forEach { (t, u) ->
                    item.addEnchantment(t,u)
                }
                target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
            } else {
                target.msg("$who §7≫ §f돈이 부족하네요.")
            }
        }

        fun shopitem(
            p: Player,
            reqlv: Int,
            itemname: String,
            mainprice: Int,
            description: String,
            type: String,
            Item: Material,
            damage: Int? = 0,
            //custom: String? = null,
            //data: Int? = 0,
        ): GuiItem {
            var permission = ""
            var who = ""
            var whoraw = ""
            var statname = ""
            when (type){
                "Fish" -> {
                    permission = "fisher.fish"
                    who = "§e어부"
                    whoraw = "어부"
                    statname = "낚시"
                }
                "Mine" -> {
                    permission = "miner.mine"
                    who = "§b광부"
                    whoraw = "광부"
                    statname = "채광"
                }
            }
            val playerlv = Stats.getStat(p, type)
            val priceint: Int = when {
                p.hasPermission(permission) -> {
                    (mainprice * 0.95).toInt()
                }
                else -> {
                    mainprice
                }
            }
            val price = "${priceint}원${
                when {
                    p.hasPermission(permission) -> " [$whoraw 할인가]"
                    else -> ""
                }
            }"
            val main = ItemStack(Item).apply {
                itemMeta = itemMeta.apply {
                    val meta = this as Damageable
                    if (damage != null) meta.damage = damage
                    /*val pdc = this.persistentDataContainer
                    if (custom != null) {
                        when (custom) {
                            "dig_speed" -> if (data != null) pdc.set(nkspeed, PersistentDataType.INTEGER,data)
                        }
                    }*/
                    displayName(
                        Component.text(itemname)
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB()))
                    )
                    lore(
                        listOf(
                            Component.text(description)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.LIME.asRGB())),
                            Component.text(""),
                            Component.text(
                                when (reqlv) {
                                    0 -> "제한 없음"
                                    else -> "요구 레벨 : $statname Lv.$reqlv 이상"
                                }
                            )
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(
                                    TextDecoration.UNDERLINED,
                                    when {
                                        playerlv >= reqlv -> false
                                        else -> true
                                    }
                                )
                                .color(
                                    when {
                                        playerlv >= reqlv -> TextColor.color(Color.AQUA.asRGB())
                                        else -> TextColor.color(Color.ORANGE.asRGB())
                                    }
                                ),
                            Component.text("구매가 : $price")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB())),
                            Component.text(""),
                            when {
                                playerlv >= reqlv -> {
                                    Component.text("구매하려면 클릭하세요!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.YELLOW.asRGB()))
                                }
                                else -> {
                                    Component.text("$statname 경력이 부족합니다!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.ORANGE.asRGB()))
                                }
                            }
                        )
                    )
                }
            }
            return GuiItem(main) {
                it.isCancelled = true
                val lvf : Boolean = playerlv >= reqlv
                when {
                    lvf -> buymessage(p,priceint,main,who)
                    else -> p.msg("$who §7≫ §f레벨이 부족합니다.")
                }
            }
        }

        fun enchantshopitem(
            p: Player,
            reqlv: Int,
            enchant: Enchantment,
            level: Int,
            enchantname: String,
            mainprice: Int,
            description: String,
            type: String,
            ApplyItem: List<Material>
        ): GuiItem {
            var permission = ""
            var who = ""
            var whoraw = ""
            var statname = ""
            when (type){
                "Fish" -> {
                    permission = "fisher.fish"
                    who = "§e어부"
                    whoraw = "어부"
                    statname = "낚시"
                }
                "Mine" -> {
                    permission = "miner.mine"
                    who = "§b광부"
                    whoraw = "광부"
                    statname = "채광"
                }
            }
            val playerlv = Stats.getStat(p, type)
            val priceint: Int = when {
                p.hasPermission(permission) -> {
                    (mainprice * 0.95).toInt()
                }
                else -> {
                    mainprice
                }
            }
            val price = "${priceint}원${
                when {
                    p.hasPermission(permission) -> " [$whoraw 할인가]"
                    else -> ""
                }
            }"
            val main = ItemStack(Material.ENCHANTED_BOOK).apply {
                itemMeta = itemMeta.apply {
                    addEnchant(enchant, level, false)
                    displayName(
                        Component.text("강화 [$enchantname]")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB()))
                    )
                    lore(
                        listOf(
                            Component.text(description)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.LIME.asRGB())),
                            Component.text(""),
                            Component.text("요구 레벨 : $statname Lv.$reqlv 이상")
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(
                                    TextDecoration.UNDERLINED,
                                    when {
                                        playerlv >= reqlv -> false
                                        else -> true
                                    }
                                )
                                .color(
                                    when {
                                        playerlv >= reqlv -> TextColor.color(Color.AQUA.asRGB())
                                        else -> TextColor.color(Color.ORANGE.asRGB())
                                    }
                                ),
                            Component.text("구매가 : $price")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB())),
                            Component.text(""),
                            when {
                                playerlv >= reqlv -> {
                                    Component.text("강화할 아이템을 들고 클릭하세요!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.YELLOW.asRGB()))
                                }
                                else -> {
                                    Component.text("$statname 경력이 부족합니다!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.ORANGE.asRGB()))
                                }
                            }
                        )
                    )
                }
            }
            return GuiItem(main) {
                it.isCancelled = true
                val target : Player = it.whoClicked as Player
                val handitem : ItemStack = it.whoClicked.itemOnCursor
                val lvf : Boolean = playerlv >= reqlv
                if (lvf && ApplyItem.contains(handitem.type)) {
                    enchantmessage(p,priceint,p.itemOnCursor,who,main)
                } else {
                    target.msg("$who §7≫ §f레벨이 부족하거나 올바르지 않은 아이템입니다.")
                }
            }
        }
    }
}