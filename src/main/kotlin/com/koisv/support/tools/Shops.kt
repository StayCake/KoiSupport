package com.koisv.support.tools

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.koisv.support.econ
import com.koisv.support.tools.Instance.Companion.rangeHarvest
import com.koisv.support.tools.Instance.Companion.rangeSoil
import com.koisv.support.tools.Stats.Companion.convert
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable

class Shops {
    companion object {
        private fun buyMessage(
            target: Player,
            price: Int,
            item: ItemStack,
            who: String,
            shift: Boolean?,
        ) {
            val money : Boolean = econ?.getBalance(target)!! >= price
            val space : Boolean = target.inventory.firstEmpty() != -1
            if (money && space) {
                if (shift == true) {
                    item.amount = 64
                    econ?.withdrawPlayer(target, (price * 64).toDouble())
                    target.inventory.addItem(item.apply {
                        itemMeta = itemMeta.apply { lore(listOf(lore()?.get(0))) }
                    })
                    target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
                } else {
                    econ?.withdrawPlayer(target, price.toDouble())
                    target.inventory.addItem(item.apply {
                        itemMeta = itemMeta.apply { lore(listOf(lore()?.get(0))) }
                    })
                    target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
                }
            } else if (money && !space) {
                target.msg("$who §7≫ §f가방에 들어갈 공간이 없는 듯 하네요.")
            } else {
                target.msg("$who §7≫ §f돈이 부족하네요.")
            }
        }

        private fun buyMessage(
            target: Player,
            price: Int,
            item: ItemStack,
            who: String,
            enchantFrom: ItemStack
        ) {
            val money : Boolean = econ?.getBalance(target)!! >= price
            if (money) {
                econ?.withdrawPlayer(target, price.toDouble())
                enchantFrom.enchantments.forEach { (t, u) ->
                    item.addEnchantment(t, u)
                }
                target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
            } else {
                target.msg("$who §7≫ §f돈이 부족하네요.")
            }
        }

        private fun buyMessage(
            target: Player,
            price: Int,
            item: ItemStack,
            who: String,
            enchantFrom: NamespacedKey,
            level: Int
        ) {
            val money : Boolean = econ?.getBalance(target)!! >= price
            if (money) {
                econ?.withdrawPlayer(target, price.toDouble())
                when (enchantFrom) {
                    rangeSoil.key -> rangeSoil.apply(item, level)
                    rangeHarvest.key -> rangeHarvest.apply(item, level)
                }
                target.msg("$who §7≫ §f잘 선택하셨습니다! §7[잔액 : ${econ?.getBalance(target)?.toInt()}원]")
            } else {
                target.msg("$who §7≫ §f돈이 부족하네요.")
            }
        }

        fun shopItem(
            p: Player,
            reqLV: Int,
            itemName: String?,
            originalPrice: Int,
            description: String?,
            type: String,
            Item: Material,
            damage: Int? = 0,
        ): GuiItem {
            val shopStat = convert(type,p,originalPrice)
            val price = shopStat[0]
            val who = shopStat[1]
            val statName = shopStat[2]
            val playerLV = Stats.getStat(p, type)
            val priceInt = price.filter{ it.isDigit() }.toInt()
            val main = ItemStack(Item).apply {
                itemMeta = itemMeta.apply {
                    val meta = this as Damageable
                    if (damage != null) meta.damage = damage
                    if (itemName != null) {
                        displayName(
                            Component.text(itemName)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB()))
                        )
                    }
                    lore(
                        listOf(
                            Component.text(description ?: "")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.LIME.asRGB())),
                            Component.text(""),
                            Component.text(
                                when (reqLV) {
                                    0 -> "제한 없음"
                                    else -> "요구 레벨 : $statName Lv.$reqLV 이상"
                                }
                            )
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(
                                    TextDecoration.UNDERLINED,
                                    when {
                                        playerLV >= reqLV -> false
                                        else -> true
                                    }
                                )
                                .color(
                                    when {
                                        playerLV >= reqLV -> TextColor.color(Color.AQUA.asRGB())
                                        else -> TextColor.color(Color.ORANGE.asRGB())
                                    }
                                ),
                            Component.text("구매가 : $price")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB())),
                            Component.text(""),
                            when {
                                playerLV >= reqLV -> {
                                    Component.text("구매하려면 클릭하세요!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.YELLOW.asRGB()))
                                }
                                else -> {
                                    Component.text("$statName 경력이 부족합니다!")
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
                val lvf : Boolean = playerLV >= reqLV
                when {
                    lvf -> buyMessage(p,priceInt,main,who,it.isShiftClick)
                    else -> p.msg("$who §7≫ §f레벨이 부족합니다.")
                }
            }
        }

        fun shopItem(
            p: Player,
            reqLV: Int,
            enchant: Enchantment,
            level: Int,
            enchantName: String,
            originalPrice: Int,
            description: String,
            type: String,
            ApplyItem: List<Material>
        ): GuiItem {
            val shopStat = convert(type,p,originalPrice)
            val price = shopStat[0]
            val who = shopStat[1]
            val statName = shopStat[2]
            val playerLV = Stats.getStat(p, type)
            val priceInt = price.filter{ it.isDigit() }.toInt()
            val main = ItemStack(Material.ENCHANTED_BOOK).apply {
                itemMeta = itemMeta.apply {
                    addEnchant(enchant, level, false)
                    displayName(
                        Component.text("강화 [$enchantName]")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB()))
                    )
                    lore(
                        listOf(
                            Component.text(description)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.LIME.asRGB())),
                            Component.text(""),
                            Component.text("요구 레벨 : $statName Lv.$reqLV 이상")
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(
                                    TextDecoration.UNDERLINED,
                                    when {
                                        playerLV >= reqLV -> false
                                        else -> true
                                    }
                                )
                                .color(
                                    when {
                                        playerLV >= reqLV -> TextColor.color(Color.AQUA.asRGB())
                                        else -> TextColor.color(Color.ORANGE.asRGB())
                                    }
                                ),
                            Component.text("구매가 : $price")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB())),
                            Component.text(""),
                            when {
                                playerLV >= reqLV -> {
                                    Component.text("강화할 아이템을 들고 클릭하세요!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.YELLOW.asRGB()))
                                }
                                else -> {
                                    Component.text("$statName 경력이 부족합니다!")
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
                val lvf : Boolean = playerLV >= reqLV
                if (lvf && ApplyItem.contains(handitem.type)) {
                    buyMessage(p,priceInt,p.itemOnCursor,who,main)
                } else {
                    target.msg("$who §7≫ §f레벨이 부족하거나 올바르지 않은 아이템입니다.")
                }
            }
        }

        fun shopItem(
            p: Player,
            reqLV: Int,
            enchantKey: NamespacedKey,
            level: Int,
            originalPrice: Int,
            description: String,
            type: String
        ): GuiItem {
            val shopStat = convert(type,p,originalPrice)
            val price = shopStat[0]
            val who = shopStat[1]
            val statName = shopStat[2]
            val playerLV = Stats.getStat(p, type)
            val priceInt = price.filter{ it.isDigit() }.toInt()
            val enchantName = when (enchantKey) {
                rangeSoil.key -> {
                    ChatColor.stripColor(rangeSoil.displayName(level))
                }
                rangeHarvest.key -> {
                    ChatColor.stripColor(rangeHarvest.displayName(level))
                }
                else -> "null"
            }
            val applyItem = when (enchantKey) {
                rangeSoil.key -> rangeSoil.canEnchant
                rangeHarvest.key -> rangeHarvest.canEnchant
                else -> listOf()
            }
            val main = ItemStack(Material.ENCHANTED_BOOK).apply {
                itemMeta = itemMeta.apply {
                    displayName(
                        Component.text("강화 [$enchantName]")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB()))
                    )
                    lore(
                        listOf(
                            Component.text(description)
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.LIME.asRGB())),
                            Component.text(""),
                            Component.text("요구 레벨 : $statName Lv.$reqLV 이상")
                                .decoration(TextDecoration.ITALIC, false)
                                .decoration(
                                    TextDecoration.UNDERLINED,
                                    when {
                                        playerLV >= reqLV -> false
                                        else -> true
                                    }
                                )
                                .color(
                                    when {
                                        playerLV >= reqLV -> TextColor.color(Color.AQUA.asRGB())
                                        else -> TextColor.color(Color.ORANGE.asRGB())
                                    }
                                ),
                            Component.text("구매가 : $price")
                                .decoration(TextDecoration.ITALIC, false)
                                .color(TextColor.color(Color.AQUA.asRGB())),
                            Component.text(""),
                            when {
                                playerLV >= reqLV -> {
                                    Component.text("강화할 아이템을 들고 클릭하세요!")
                                        .decoration(TextDecoration.ITALIC, false)
                                        .color(TextColor.color(Color.YELLOW.asRGB()))
                                }
                                else -> {
                                    Component.text("$statName 경력이 부족합니다!")
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
                val lvf : Boolean = playerLV >= reqLV
                if (lvf && applyItem.contains(handitem.type)) {
                    buyMessage(p,priceInt,p.itemOnCursor,who,enchantKey,level)
                } else {
                    target.msg("$who §7≫ §f레벨이 부족하거나 올바르지 않은 아이템입니다.")
                }
            }
        }
    }
}