package com.koisv.support.misc.tools

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.econ
import com.koisv.support.jobs.Farmer
import com.koisv.support.jobs.Fisher
import com.koisv.support.jobs.Miner
import com.koisv.support.misc.tools.Instance.Companion.rangeHarvest
import com.koisv.support.misc.tools.Instance.Companion.rangeSoil
import com.koisv.support.misc.tools.Stats.Companion.convert
import hazae41.minecraft.kutils.bukkit.keys
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.section
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import kotlin.math.ceil

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
                val handItem : ItemStack = it.whoClicked.itemOnCursor
                val lvf : Boolean = playerLV >= reqLV
                if (lvf && ApplyItem.contains(handItem.type)) {
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

        fun rightClick (e: PlayerInteractEntityEvent) {
            val eh: EquipmentSlot = e.hand
            val custom = Instance.customShop.keys.contains(e.rightClicked.name)
            if (eh == EquipmentSlot.HAND) {
                when (e.rightClicked.name) {
                    "§b어부" -> {
                        if (!e.player.isSneaking) {
                            e.player.msg(
                                """
                        |§e어부 §7≫ §f안녕하세요!
                        |§e어부 §7≫ §f팔 물건을 들고 좌클릭하세요.
                        |§e어부 §7≫ §f웅크리고 있다면 한번에 팔 수 있어요. [보물 제외]
                        |§e어부 §7≫ §f아, 낚시용품만 받아요. 그리고 물건을 사고 싶으면 웅크리고 불러주세요!""".trimMargin()
                            )
                        } else {
                            val gui = Fisher.fishGui(e.player)
                            gui.show(e.player)
                        }
                    }
                    "§7광부" -> {
                        if (!e.player.isSneaking) {
                            e.player.msg(
                                """
                        |§7광부 §7≫ §f안녕 친구!
                        |§7광부 §7≫ §f팔 물건을 들고 좌클릭하라고.
                        |§7광부 §7≫ §f웅크리고 있다면 한번에 팔 수 있다네.
                        |§7광부 §7≫ §f광물만 받는다네. 그리고 물건을 사고 싶으면 웅크리고 불러주게!""".trimMargin()
                            )
                        } else {
                            val gui = Miner.mineGui(e.player)
                            gui.show(e.player)
                        }
                    }
                    "§7수중 광부" -> {
                        e.player.msg(
                            """
                        |§7광부 §7≫ §f안녕하신가!
                        |§7광부 §7≫ §f이쯤까지 왔으니 이곳을 잘 알지 않는가.
                        |§7광부 §7≫ §f여기서부턴 물건 값이 달라진다는 것만 잘 알아두고,
                        |§7광부 §7≫ §f이젠 불러만 주게.""".trimMargin()
                        )
                        val gui = Miner.mineGui(e.player)
                        gui.show(e.player)
                    }
                    "§7폐광 광부" -> {
                        val gui = Miner.mineGui(e.player)
                        gui.show(e.player)
                    }
                    "§7심층 광부" -> {
                        val gui = Miner.mineGui(e.player)
                        gui.show(e.player)
                    }
                    "§a농부" -> {
                        if (!e.player.isSneaking) {
                            e.player.msg(
                                """
                        |§e농부 §7≫ §f여어!
                        |§e농부 §7≫ §f팔 물건이 있으면 주라고.
                        |§e농부 §7≫ §f웅크리고 있다면 한번에 받아 주지.
                        |§e농부 §7≫ §f농산물만 줘. 웅크리고 우클릭해서 물건을 봐도 좋고!""".trimMargin()
                            )
                        } else {
                            val gui = Farmer.farmGui(e.player)
                            gui.show(e.player)
                        }
                    }
                    else -> {
                        if (custom) {
                            val t = Instance.customShop.section(e.rightClicked.name)?.getKeys(false)
                            println(t)
                            if (t != null) {
                                val cShop = ChestGui(ceil((t.size.toFloat()) /9).toInt(), e.rightClicked.name)
                                val cPane = StaticPane(9, ceil((t.size.toFloat()) /9).toInt())
                                var index = 0
                                var line = 0
                                t.forEach { m ->
                                    if (index > 8) {
                                        index = 0
                                        line++
                                    }
                                    val tm = Material.getMaterial(m)
                                    if (tm != null) {
                                        cPane.addItem(shopItem(e.player,0,null, Instance.customShop.section(e.rightClicked.name)?.getInt(m) ?: 0,null,"shop",tm,null),index,line)
                                    }
                                    index++
                                }
                                cShop.addPane(cPane)
                                cShop.show(e.player)
                            }
                        }
                    }
                }
            }
        }
        fun leftClick (e: EntityDamageByEntityEvent) {
            if (e.damager.type == EntityType.PLAYER) {
                val p = e.damager as Player
                val pmh = p.inventory.itemInMainHand
                val fish = p.hasPermission("fisher.fish")
                val mine = p.hasPermission("miner.mine")
                var finalCost = 0
                var check = false
                var target = ""
                when (e.entity.name) {
                    "${ChatColor.AQUA}어부" -> {
                        target = "§e어부"
                        if (p.isSneaking){
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val item: ItemStack = it
                                    val mi = i.contents.indexOf(item)
                                    val amt = item.amount
                                    val cost = Fisher.getFishCost(item.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (fish) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val im = pmh.itemMeta
                            val cost = Fisher.getFishCost(pmh.type)
                            when (im){
                                Fisher.fishTreasure.itemMeta -> {
                                    check = true
                                    Fisher.fishTreasure(p,pmh.amount)
                                }
                                Fisher.fishTrash.itemMeta -> {
                                    check = true
                                    Fisher.fishTrash(p,pmh.amount)
                                }
                                else -> {
                                    if (cost > 0) {
                                        p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                                        finalCost += if (fish) (cost * amt * 1.05).toInt() else cost * amt
                                    }
                                }
                            }
                        }
                    }
                    "${ChatColor.GRAY}광부" -> {
                        target = "§7광부"
                        if (p.isSneaking) {
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val mh: ItemStack = it
                                    val mi = i.contents.indexOf(mh)
                                    val amt = mh.amount
                                    val cost = Miner.getMine1Cost(mh.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val cost = Miner.getMine1Cost(pmh.type)
                            if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                            finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                        }
                    }
                    "${ChatColor.GRAY}수중 광부" -> {
                        target = "§7광부"
                        if (p.isSneaking) {
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val mh: ItemStack = it
                                    val mi = i.contents.indexOf(mh)
                                    val amt = mh.amount
                                    val cost = Miner.getMine2Cost(mh.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val cost = Miner.getMine2Cost(pmh.type)
                            if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                            finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                        }
                    }
                    "${ChatColor.GRAY}폐광 광부" -> {
                        target = "§7광부"
                        if (p.isSneaking) {
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val mh: ItemStack = it
                                    val mi = i.contents.indexOf(mh)
                                    val amt = mh.amount
                                    val cost = Miner.getMine3Cost(mh.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val cost = Miner.getMine3Cost(pmh.type)
                            if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                            finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                        }
                    }
                    "${ChatColor.GRAY}심층 광부" -> {
                        target = "§7광부"
                        if (p.isSneaking) {
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val mh: ItemStack = it
                                    val mi = i.contents.indexOf(mh)
                                    val amt = mh.amount
                                    val cost = Miner.getMine4Cost(mh.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val cost = Miner.getMine4Cost(pmh.type)
                            if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                            finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                        }
                    }
                    "${ChatColor.GREEN}농부" -> {
                        target = "§e농부"
                        if (p.isSneaking) {
                            val i : Inventory = p.inventory
                            i.forEach {
                                if (it != null) {
                                    val mh: ItemStack = it
                                    val mi = i.contents.indexOf(mh)
                                    val amt = mh.amount
                                    val cost = Farmer.getCropCost(mh.type)
                                    if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                    finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                                }
                            }
                        } else {
                            val amt = pmh.amount
                            val cost = Farmer.getCropCost(pmh.type)
                            if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                            finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                        }
                    }
                    else -> check = true
                }
                if (!check) {
                    if (finalCost == 0) {
                        p.msg("$target §7≫ §f판매 가능한 물건만 주세요...")
                    } else {
                        econ?.depositPlayer(p, finalCost.toDouble())
                        p.msg("$target §7≫ §f${finalCost}원에 받을게요.")
                    }
                }
            }
        }
    }
}