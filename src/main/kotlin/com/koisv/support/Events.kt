package com.koisv.support

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.support.jobs.Farmer
import com.koisv.support.jobs.Fisher
import com.koisv.support.jobs.Miner
import com.koisv.support.jobs.WoodCutter.Companion.axe
import com.koisv.support.tools.Instance.Companion.config
import com.koisv.support.tools.Instance.Companion.customshop
import com.koisv.support.tools.Shops.Companion.shopItem
import com.koisv.support.tools.Stats
import hazae41.minecraft.kutils.bukkit.keys
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.bukkit.section
import hazae41.minecraft.kutils.textOf
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound.Source
import net.kyori.adventure.sound.Sound.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil
import kotlin.math.round

class Events : Listener {

    companion object {
        fun test() {
            val inv = Bukkit.createInventory(null,InventoryType.CREATIVE)
            Bukkit.getOnlinePlayers().forEach {
                it.openInventory(inv)
            }
        }
    }

    @EventHandler
    fun woodcutter(e: BlockPlaceEvent) {
        if (e.blockReplacedState.type == Material.OXIDIZED_CUT_COPPER_SLAB) {
            val mh = e.player.inventory.itemInMainHand
            val oh = e.player.inventory.itemInOffHand
            when {
                axe.contains(mh.type) -> {
                    if (oh == ItemStack(Material.DIAMOND,oh.amount)) {
                        e.isCancelled = true
                        oh.amount -= 1
                        mh.addUnsafeEnchantment(Enchantment.DIG_SPEED, (mh.enchantments[Enchantment.DIG_SPEED] ?: 0) + 1)
                        e.player.msg("강화!")
                    } else {
                        e.player.msg("강화 재료가 없습니다.")
                    }
                }
            }
        }
    }

    @EventHandler
    fun gotcha(e: PlayerFishEvent) {
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
                        sound(
                            Key.key("entity.experience_orb.pickup"),
                            Source.PLAYER,
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
                        sound(
                            Key.key("entity.player.levelup"),
                            Source.PLAYER,
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
                    e.player.playSound(sound(Key.key("ui.toast.challenge_complete"), Source.PLAYER,1F, 0.7F))
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
                    val scap = Math.random() * 100
                    val length: Float = when {
                        scap >= 46 -> {
                            (round((0.01 + Math.random() * (49.99 - 0.01))) * 100 / 100).toFloat()
                        }
                        scap >= 26 -> {
                            (round((50 + Math.random() * (99.99 - 50))) * 100 / 100).toFloat()
                        }
                        scap >= 11 -> {
                            (round((100 + Math.random() * (149.99 - 100))) * 100 / 100).toFloat()
                        }
                        scap >= 6 -> {
                            (round((150 + Math.random() * (199.99 - 150))) * 100 / 100).toFloat()
                        }
                        scap >= 3 -> {
                            (round((200 + Math.random() * (209.99 - 200))) * 100 / 100).toFloat()
                        }
                        scap >= 1 -> {
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
                        e.player.playSound(sound(Key.key("entity.player.levelup"), Source.PLAYER,1F, 0.5F))
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
                    e.expToDrop = (e.expToDrop * config.getDouble("values.multiplier.fisher")).toInt()
                    Stats.setStat(e.expToDrop, e.player, "Fish")
                } else {
                    Stats.setStat(e.expToDrop, e.player, "Fish")
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
                            item.itemStack = Fisher.fishTreasure
                        }
                        else {
                            item.itemStack = Fisher.fishTrash
                        }
                    }
                    1 -> {
                        val lrv = round(1 + Math.random() * (152 - 1))
                        if (lrv <= 71) {
                            item.itemStack = Fisher.fishTreasure
                        }
                        else {
                            item.itemStack = Fisher.fishTrash
                        }
                    }
                    2 -> {
                        val lrv = round(1 + Math.random() * (153 - 1))
                        if (lrv <= 92) {
                            item.itemStack = Fisher.fishTreasure
                        }
                        else {
                            item.itemStack = Fisher.fishTrash
                        }
                    }
                    3 -> {
                        val lrv = round(1 + Math.random() * (155 - 1))
                        if (lrv <= 113) {
                            item.itemStack = Fisher.fishTreasure
                        }
                        else {
                            item.itemStack = Fisher.fishTrash
                        }
                    }
                }
            }
            Stats.showStat(e.player,"Fish")
        }
        if (e.state == PlayerFishEvent.State.FAILED_ATTEMPT || e.state == PlayerFishEvent.State.REEL_IN || e.state == PlayerFishEvent.State.IN_GROUND) {
            e.player.sendActionBar(
                Component.text().content("아무것도 낚지 못한 듯 합니다...").build()
            )
        }
    }

    @EventHandler
    fun minexp(e: BlockBreakEvent) {
        if (e.player.gameMode != GameMode.CREATIVE) {
            val p = e.player
            if (Miner.nonexpore.contains(e.block.type)) {
                if (e.block.isValidTool(p.inventory.itemInOffHand) || e.block.isValidTool(p.inventory.itemInMainHand)) {
                    when (e.block.type) {
                        Material.COPPER_ORE -> {
                            e.expToDrop = round(0.9 + Math.random() * (2.0 - 0.9)).toInt()
                        }
                        Material.IRON_ORE -> {
                            e.expToDrop = 1
                        }
                        Material.GOLD_ORE -> {
                            e.expToDrop = 1
                        }
                        Material.ANCIENT_DEBRIS -> {
                            e.expToDrop = round(5 + Math.random() * (10 - 5)).toInt()
                        }
                        else -> {}
                    }
                }
            }
            if (e.player.hasPermission("miner.mine")) {
                e.expToDrop = (e.expToDrop * config.getDouble("values.multiplier.miner")).toInt()
            }
            if (e.expToDrop > 0) {
                Stats.setStat(e.expToDrop, p, "Mine")
                Stats.showStat(p, "Mine")
            }
        }
    }

    @EventHandler
    fun interactNPC(e: PlayerInteractEntityEvent) {
        val eh: EquipmentSlot = e.hand
        val custom = customshop.keys.contains(e.rightClicked.name)
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
                        val gui = Fisher.fishgui(e.player)
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
                        val gui = Miner.minegui(e.player)
                        gui.show(e.player)
                    }
                }
                "§7협곡 광부" -> {
                    e.player.msg(
                        """
                        |§7광부 §7≫ §f안녕하신가!
                        |§7광부 §7≫ §f이쯤까지 왔으니 이곳을 잘 알지 않는가.
                        |§7광부 §7≫ §f여기서부턴 물건 값이 달라진다는 것만 잘 알아두고,
                        |§7광부 §7≫ §f이젠 불러만 주게.""".trimMargin()
                    )
                    val gui = Miner.minegui(e.player)
                    gui.show(e.player)
                }
                "§7폐광 광부" -> {
                    val gui = Miner.minegui(e.player)
                    gui.show(e.player)
                }
                "§7심층 광부" -> {
                    val gui = Miner.minegui(e.player)
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
                        val t = customshop.section(e.rightClicked.name)?.getKeys(false)
                        println(t)
                        if (t != null) {
                            val cShop = ChestGui(ceil((t.size.toFloat()) /9).toInt(), e.rightClicked.name)
                            val cPane = StaticPane(9,ceil((t.size.toFloat()) /9).toInt())
                            var index = 0
                            var line = 0
                            t.forEach { m ->
                                if (index > 8) {
                                    index = 0
                                    line++
                                }
                                val tm = Material.getMaterial(m)
                                if (tm != null) {
                                    cPane.addItem(shopItem(e.player,0,null, customshop.section(e.rightClicked.name)?.getInt(m) ?: 0,null,"shop",tm,null),index,line)
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

    @EventHandler
    fun clickNPC(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.PLAYER) {
            val p = e.damager as Player
            val pmh = p.inventory.itemInMainHand
            val fish = p.hasPermission("fisher.fish")
            val mine = p.hasPermission("miner.mine")
            var finalcost = 0
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
                                finalcost += if (fish) (cost * amt * 1.05).toInt() else (cost * amt)
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
                                    finalcost += if (fish) (cost * amt * 1.05).toInt() else cost * amt
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
                                finalcost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine1Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalcost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                "${ChatColor.GRAY}협곡 광부" -> {
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
                                finalcost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine2Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalcost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
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
                                finalcost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine3Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalcost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
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
                                finalcost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Miner.getMine4Cost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalcost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
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
                                finalcost += if (mine) (cost * amt * 1.05).toInt() else (cost * amt)
                            }
                        }
                    } else {
                        val amt = pmh.amount
                        val cost = Farmer.getCropCost(pmh.type)
                        if (cost != 0) p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                        finalcost += if (mine) (cost * amt * 1.05).toInt() else cost * amt
                    }
                }
                else -> check = true
            }
            if (!check) {
                if (finalcost == 0) {
                    p.msg("$target §7≫ §f판매 가능한 물건만 주세요...")
                } else {
                    econ?.depositPlayer(p, finalcost.toDouble())
                    p.msg("$target §7≫ §f${finalcost}원에 받을게요.")
                }
            }
        }
    }

}