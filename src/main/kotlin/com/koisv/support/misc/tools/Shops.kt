package com.koisv.support.misc.tools

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.econ
import com.koisv.support.jobs.Farmer
import com.koisv.support.jobs.Fisher
import com.koisv.support.jobs.Miner
import com.koisv.support.jobs.WoodCutter
import com.koisv.support.misc.tools.Instance.rangeHarvest
import com.koisv.support.misc.tools.Instance.rangeSoil
import com.koisv.support.misc.tools.Stats.convert
import com.koisv.support.misc.tools.Stats.getStat
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

object Shops {
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
                target.msg("$who ??7??? ??f??? ?????????????????????! ??7[?????? : ${econ?.getBalance(target)?.toInt()}???]")
            } else {
                econ?.withdrawPlayer(target, price.toDouble())
                target.inventory.addItem(item.apply {
                    itemMeta = itemMeta.apply { lore(listOf(lore()?.get(0))) }
                })
                target.msg("$who ??7??? ??f??? ?????????????????????! ??7[?????? : ${econ?.getBalance(target)?.toInt()}???]")
            }
        } else if (money && !space) {
            target.msg("$who ??7??? ??f????????? ????????? ????????? ?????? ??? ?????????.")
        } else {
            target.msg("$who ??7??? ??f?????? ???????????????.")
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
            target.msg("$who ??7??? ??f??? ?????????????????????! ??7[?????? : ${econ?.getBalance(target)?.toInt()}???]")
        } else {
            target.msg("$who ??7??? ??f?????? ???????????????.")
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
            target.msg("$who ??7??? ??f??? ?????????????????????! ??7[?????? : ${econ?.getBalance(target)?.toInt()}???]")
        } else {
            target.msg("$who ??7??? ??f?????? ???????????????.")
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
        val playerLV = p.getStat(type)
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
                                0 -> "?????? ??????"
                                else -> "?????? ?????? : $statName Lv.$reqLV ??????"
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
                        Component.text("????????? : $price")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB())),
                        Component.text(""),
                        when {
                            playerLV >= reqLV -> {
                                Component.text("??????????????? ???????????????!")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Color.YELLOW.asRGB()))
                            }
                            else -> {
                                Component.text("$statName ????????? ???????????????!")
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
                else -> p.msg("$who ??7??? ??f????????? ???????????????.")
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
        val playerLV = p.getStat(type)
        val priceInt = price.filter{ it.isDigit() }.toInt()
        val main = ItemStack(Material.ENCHANTED_BOOK).apply {
            itemMeta = itemMeta.apply {
                addEnchant(enchant, level, false)
                displayName(
                    Component.text("?????? [$enchantName]")
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(Color.AQUA.asRGB()))
                )
                lore(
                    listOf(
                        Component.text(description)
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.LIME.asRGB())),
                        Component.text(""),
                        Component.text("?????? ?????? : $statName Lv.$reqLV ??????")
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
                        Component.text("????????? : $price")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB())),
                        Component.text(""),
                        when {
                            playerLV >= reqLV -> {
                                Component.text("????????? ???????????? ?????? ???????????????!")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Color.YELLOW.asRGB()))
                            }
                            else -> {
                                Component.text("$statName ????????? ???????????????!")
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
                target.msg("$who ??7??? ??f????????? ??????????????? ???????????? ?????? ??????????????????.")
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
        val playerLV = p.getStat(type)
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
                    Component.text("?????? [$enchantName]")
                        .decoration(TextDecoration.ITALIC, false)
                        .color(TextColor.color(Color.AQUA.asRGB()))
                )
                lore(
                    listOf(
                        Component.text(description)
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.LIME.asRGB())),
                        Component.text(""),
                        Component.text("?????? ?????? : $statName Lv.$reqLV ??????")
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
                        Component.text("????????? : $price")
                            .decoration(TextDecoration.ITALIC, false)
                            .color(TextColor.color(Color.AQUA.asRGB())),
                        Component.text(""),
                        when {
                            playerLV >= reqLV -> {
                                Component.text("????????? ???????????? ?????? ???????????????!")
                                    .decoration(TextDecoration.ITALIC, false)
                                    .color(TextColor.color(Color.YELLOW.asRGB()))
                            }
                            else -> {
                                Component.text("$statName ????????? ???????????????!")
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
                target.msg("$who ??7??? ??f????????? ??????????????? ???????????? ?????? ??????????????????.")
            }
        }
    }

    fun rightClick (e: PlayerInteractEntityEvent) {
        val eh: EquipmentSlot = e.hand
        val custom = Instance.customShop.keys.contains(e.rightClicked.name)
        if (eh == EquipmentSlot.HAND) {
            when (e.rightClicked.name) {
                "??b??????" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |??e?????? ??7??? ??f???????????????!
                        |??e?????? ??7??? ??f??? ????????? ?????? ??????????????????.
                        |??e?????? ??7??? ??f???????????? ????????? ????????? ??? ??? ?????????. [?????? ??????]
                        |??e?????? ??7??? ??f???, ??????????????? ?????????. ????????? ????????? ?????? ????????? ???????????? ???????????????!""".trimMargin()
                        )
                    } else {
                        val gui = Fisher.fishGui(e.player)
                        gui.show(e.player)
                    }
                }
                "??7??????" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |??7?????? ??7??? ??f?????? ??????!
                        |??7?????? ??7??? ??f??? ????????? ?????? ??????????????????.
                        |??7?????? ??7??? ??f???????????? ????????? ????????? ??? ??? ?????????.
                        |??7?????? ??7??? ??f????????? ????????????. ????????? ????????? ?????? ????????? ???????????? ????????????!""".trimMargin()
                        )
                    } else {
                        val gui = Miner.mineGui(e.player)
                        gui.show(e.player)
                    }
                }
                "??7?????? ??????" -> {
                    e.player.msg(
                        """
                        |??7?????? ??7??? ??f???????????????!
                        |??7?????? ??7??? ??f???????????? ????????? ????????? ??? ?????? ?????????.
                        |??7?????? ??7??? ??f??????????????? ?????? ?????? ??????????????? ?????? ??? ????????????,
                        |??7?????? ??7??? ??f?????? ????????? ??????.""".trimMargin()
                    )
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "??7?????? ??????" -> {
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "??7?????? ??????" -> {
                    val gui = Miner.mineGui(e.player)
                    gui.show(e.player)
                }
                "??a??????" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |??e?????? ??7??? ??f??????!
                        |??e?????? ??7??? ??f??? ????????? ????????? ?????????.
                        |??e?????? ??7??? ??f???????????? ????????? ????????? ?????? ??????.
                        |??e?????? ??7??? ??f???????????? ???. ???????????? ??????????????? ????????? ?????? ??????!""".trimMargin()
                        )
                    } else {
                        val gui = Farmer.farmGui(e.player)
                        gui.show(e.player)
                    }
                }
                "?????????" -> {
                    if (!e.player.isSneaking) {
                        e.player.msg(
                            """
                        |??d????????? ??7??? ??f??????!
                        |??d????????? ??7??? ??f?????? ????????? ????????? ?????? ?????????
                        |??d????????? ??7??? ??f???????????? ???????????? ?????? ??????.
                        |??d????????? ??7??? ??f?????? ???. ???????????? ??????????????? ????????? ?????? ??????!""".trimMargin()
                        )
                    } else {
                        val gui = WoodCutter.woodGui(e.player)
                        gui.show(e.player)
                    }
                }
                else -> {
                    if (custom) {
                        val t = Instance.customShop.section(e.rightClicked.name)?.getKeys(false)
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
                "${ChatColor.AQUA}??????" -> {
                    target = "??e??????"
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
                "${ChatColor.GRAY}??????" -> {
                    target = "??7??????"
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
                "${ChatColor.GRAY}?????? ??????" -> {
                    target = "??7??????"
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
                "${ChatColor.GRAY}?????? ??????" -> {
                    target = "??7??????"
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
                "${ChatColor.GRAY}?????? ??????" -> {
                    target = "??7??????"
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
                "${ChatColor.GREEN}??????" -> {
                    target = "??e??????"
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
                "?????????" -> {
                    target = "??d?????????"
                    if (p.isSneaking) {
                        val i : Inventory = p.inventory
                        i.forEach {
                            if (it != null) {
                                val mh: ItemStack = it
                                val mi = i.contents.indexOf(mh)
                                val amt = mh.amount
                                val cost = WoodCutter.getWoodCost(mh.type)
                                if (cost != 0) p.inventory.setItem(mi,ItemStack(Material.AIR))
                                finalCost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = WoodCutter.getWoodCost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalCost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                else -> check = true
            }
            if (!check) {
                if (finalCost == 0) {
                    p.msg("$target ??7??? ??f?????? ????????? ????????? ?????????...")
                } else {
                    econ?.depositPlayer(p, finalCost.toDouble())
                    p.msg("$target ??7??? ??f${finalCost}?????? ????????????.")
                }
            }
        }
    }
}