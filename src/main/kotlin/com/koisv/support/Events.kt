package com.koisv.support

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.textOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.*
import org.bukkit.ChatColor.AQUA
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.round


class Events : Listener {

    private val pickaxe = listOf(
        Material.WOODEN_PICKAXE,
        Material.STONE_PICKAXE,
        Material.IRON_PICKAXE,
        Material.GOLDEN_PICKAXE,
        Material.DIAMOND_PICKAXE,
        Material.NETHERITE_PICKAXE
    )

    private val nonexpore = listOf(
        Material.COPPER_ORE,
        Material.IRON_ORE,
        Material.GOLD_ORE,
        Material.ANCIENT_DEBRIS
    )

    private fun getInstance(): Plugin {
        return Main.instance
    }
    
    private val config = getInstance().config

    private val nkspeed = NamespacedKey(getInstance(),"key-effect-speed")

    private val treasure = ItemStack(Material.BRICK).apply {
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

    private val trash = ItemStack(Material.BOWL).apply {
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

    private fun shopitem(
        p: Player,
        reqlv: Int,
        itemname: String,
        mainprice: Int,
        description: String,
        type: String,
        Item: Material,
        damage: Int? = 0,
        custom: String? = null,
        data: Int? = 0,
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
        val playerlv = getStat(p, type)
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
                val pdc = this.persistentDataContainer
                if (custom != null) {
                    when (custom) {
                        "dig_speed" -> if (data != null) pdc.set(nkspeed, PersistentDataType.INTEGER,data)
                    }
                }
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
                lvf -> buymessage(p,mainprice,main,who)
                else -> p.msg("$who §7≫ §f레벨이 부족합니다.")
            }
        }
    }

    private fun enchantshopitem(
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
        val playerlv = getStat(p, type)
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

    private fun getStats(): YamlConfiguration {
        return Main.stats
    }

    private fun getStatsloc(): File {
        return Main.statsloc
    }

    private fun setStat(
        d: Int,
        p: Player,
        t: String
    ) {
        if (getStats().isInt("${p.uniqueId}.${t}Exp")){
            val prd = getStats().getInt("${p.uniqueId}.${t}Exp")
            expcalc(d + prd,getStats().getInt("${p.uniqueId}.${t}LV"), p,
                when (t) {
                    "Fish" -> "fisher"
                    "Farm" -> "farmer"
                    "Mine" -> "miner"
                    else -> "null"
                }
            )
            getStats().set("${p.uniqueId}.${t}Exp",d + prd)
            getStats().save(getStatsloc())
        } else {
            getStats().set("${p.uniqueId}.${t}LV",1)
            getStats().set("${p.uniqueId}.${t}Exp",d)
            getStats().save(getStatsloc())
        }
    }

    private fun getStat(
        p: Player,
        t: String
    ): Int {
        return if (getStats().isInt("${p.uniqueId}.${t}LV")){
            getStats().getInt("${p.uniqueId}.${t}LV")
        } else {
            0
        }
    }
    
    private fun showStat(p : Player, t: String){
        val lv = getStat(p,t)
        val type = when (t) {
            "Fish" -> "fisher"
            "Mine" -> "miner"
            "Farm" -> "farmer"
            else -> ""
        }
        val first = config.getInt("values.firstexp.$type")
        val term = config.getInt("values.expterm.$type")
        val max = lv * ( first + ( first + ( term * (lv-1) ) ) )/ 2
        val exp = if (getStats().isInt("${p.uniqueId}.${t}Exp")){
            getStats().getInt("${p.uniqueId}.${t}Exp")
        } else {
            0
        }
        val m = when (t) {
            "Fish" -> "낚시"
            "Mine" -> "채광"
            "Farm" -> "농사"
            else -> ""
        }
        p.sendActionBar(Component.text("$m Lv.$lv | $exp / $max"))
    }

    private fun expcalc(i: Int, cl: Int, t: Player, type: String) {
        val first = config.getInt("values.firstexp.$type")
        val term = config.getInt("values.expterm.$type")
        var explv = cl * ( first + ( first + ( term * (cl-1) ) ) )/ 2
        val typetext = when (type) {
            "Fish" -> "§e어부"
            "Farm" -> "§a농부"
            "Mine" -> "§b광부"
            else -> ""
        }
        var ccl = cl
        var ul = 0
        while (i > explv) {
            ccl++
            explv = ccl * ( first + ( first + ( term * (ccl-1) ) ) )/ 2
            ul++
        }
        if (ul != 0) {
            t.msg("$typetext §7≫ §aLevel Up! Lv.$cl -> Lv.${cl + ul}")
            t.playSound(
                t.location, Sound.ENTITY_PLAYER_LEVELUP, (1.0).toFloat(), (1.0).toFloat()
            )
            val ts = when (type) {
                "fisher" -> "Fish"
                "farmer" -> "Farm"
                "miner" -> "Mine"
                else -> ""
            }
            getStats().set("${t.uniqueId}.${ts}LV",(cl + ul))
            getStats().save(getStatsloc())
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
                        e.player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, (1.0).toFloat(), (1.0).toFloat()
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
                        e.player.location, Sound.ENTITY_PLAYER_LEVELUP, (1.0).toFloat(), (1.0).toFloat()
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
                        e.player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, (1.0).toFloat(), (0.7).toFloat()
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
                        e.player.playSound(
                            e.player.location, Sound.ENTITY_PLAYER_LEVELUP, (1.0).toFloat(), (0.5).toFloat()
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
                    e.expToDrop = (e.expToDrop * config.getDouble("values.multiplier.fisher")).toInt()
                    setStat(e.expToDrop, e.player, "Fish")
                } else {
                    setStat(e.expToDrop, e.player, "Fish")
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
                            item.itemStack = treasure
                        }
                        else {
                            item.itemStack = trash
                        }
                    }
                    1 -> {
                        val lrv = round(1 + Math.random() * (152 - 1))
                        if (lrv <= 71) {
                            item.itemStack = treasure
                        }
                        else {
                            item.itemStack = trash
                        }
                    }
                    2 -> {
                        val lrv = round(1 + Math.random() * (153 - 1))
                        if (lrv <= 92) {
                            item.itemStack = treasure
                        }
                        else {
                            item.itemStack = trash
                        }
                    }
                    3 -> {
                        val lrv = round(1 + Math.random() * (155 - 1))
                        if (lrv <= 113) {
                            item.itemStack = treasure
                        }
                        else {
                            item.itemStack = trash
                        }
                    }
                }
            }
            showStat(e.player,"Fish")
        }
        if (e.state == PlayerFishEvent.State.FAILED_ATTEMPT || e.state == PlayerFishEvent.State.REEL_IN || e.state == PlayerFishEvent.State.IN_GROUND) {
            e.player.sendActionBar(
                Component.text().content("아무것도 낚지 못한 듯 합니다...").build()
            )
        }
    }

    private var instblock: Block? = null

    @EventHandler
    fun checkmine(d: BlockDamageEvent) {
        instblock = d.block
    }

    @EventHandler
    fun minexp(e: BlockBreakEvent) {
        if (instblock == e.block) {
            val p = e.player
            if (nonexpore.contains(e.block.type)) {
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
                    else -> {
                    }
                }
            }
            if (e.player.hasPermission("miner.mine")) {
                e.expToDrop = (e.expToDrop * config.getDouble("values.multiplier.miner")).toInt()
            }
            if (e.expToDrop > 0) {
                setStat(e.expToDrop, p, "Mine")
                showStat(p, "Mine")
            }
        }
    }

    @EventHandler
    fun digspeed(e: BlockDamageEvent) {
        if (!e.instaBreak) {
            val speed = e.itemInHand.itemMeta?.persistentDataContainer?.get(nkspeed, PersistentDataType.INTEGER)
            if (speed != null) {
                when {
                    speed > 0 -> {
                        if (e.itemInHand.itemMeta != null) {
                            val duration = e.block.getDestroySpeed(e.itemInHand, true)
                            val hardness = e.block.type.hardness
                            val finaltime = ceil(((hardness / duration) / 0.75) * 1.2.pow(speed)).toInt()
                            e.player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING,finaltime * 20,speed - 1,false,false))
                        }
                    }
                    speed < 0 -> {
                        if (e.itemInHand.itemMeta != null) {
                            val duration = e.block.getDestroySpeed(e.itemInHand, true)
                            val hardness = e.block.type.hardness
                            val spdm = when(-speed) {
                                1 -> 3.3
                                2 -> 6.65
                                3 -> 370.04
                                else -> 1234.56
                            }
                            val finaltime = ceil((hardness / duration) * spdm).toInt()
                            e.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING,finaltime * 20,-(speed + 1),false,false))
                        }
                    }
                }
            }
        }
    }

    /*private fun repair(p: Player) : GrindstoneGui{
        val main = GrindstoneGui("도구 수리하기")
        main.setOnGlobalDrag {
            it.isCancelled = true
        }
        main.setOnTopClick {
            it.isCancelled = true
            if (it.slot == 1) {
                if (it.inventory.contents[0] is Repairable) {
                    println(it.inventory.contents[0])
                    println(it.inventory.contents[1])
                    println(it.inventory.contents[2])
                }
            }
        }
        return main
    }*/
    private fun minegui(p: Player) : ChestGui {
        val mineShop = ChestGui(3,"§b광산용품 상점")
        mineShop.setOnGlobalClick {
            if (it.isShiftClick) it.isCancelled = true
        }
        mineShop.setOnTopClick {
            it.isCancelled = true
        }
        mineShop.setOnGlobalDrag {
            it.isCancelled = true
        }
        val p1 = shopitem(p,0,"부셔진 곡괭이",70000,"\"아니, 캐지기는 하는 거야?\"","Mine",Material.WOODEN_PICKAXE)
        val p2 = shopitem(p,4,"닳은 곡괭이",150000,"거의 못 쓸 수준이다.","Mine",Material.STONE_PICKAXE)
        val p3 = shopitem(p,8,"중고 곡괭이",350000,"그나마 쓸 만한 녀석이다.","Mine",Material.GOLDEN_PICKAXE,-200)
        val p4 = shopitem(p,14,"곡괭이",600000,"철물점에서 본 듯한 녀석이다.","Mine",Material.IRON_PICKAXE)
        val p5 = shopitem(p,20,"전문 곡괭이",1250000,"관리되고 있는 녀석이라고 한다.","Mine",Material.DIAMOND_PICKAXE)
        val p6 = shopitem(p,25,"장인 곡괭이",2750000,"직접 갈고닦은 녀석이라고 한다.","Mine",Material.NETHERITE_PICKAXE)
        val md = enchantshopitem(p, 35, Enchantment.MENDING, 1, "수선", 3500000, "이젠 무한의 시대.","Mine", pickaxe)
        val u1 = enchantshopitem(p, 2, Enchantment.DURABILITY, 1, "내구성 I", 50000,"자그마한 납땜.","Mine", pickaxe)
        val u2 = enchantshopitem(p, 6, Enchantment.DURABILITY, 2, "내구성 II", 150000,"철판 덧대기.","Mine", pickaxe)
        val u3 = enchantshopitem(p, 18, Enchantment.DURABILITY, 3, "내구성 III", 250000,"망치질 추가하기.","Mine", pickaxe)
        val l1 = enchantshopitem(p, 5, Enchantment.LOOT_BONUS_BLOCKS, 1, "행운 I", 85000,"네잎 클로버를 찾았다.","Mine", pickaxe)
        val l2 = enchantshopitem(p, 12, Enchantment.LOOT_BONUS_BLOCKS, 2, "행운 II", 190000,"행운이 함께하길 빌었다.","Mine", pickaxe)
        val l3 = enchantshopitem(p, 21, Enchantment.LOOT_BONUS_BLOCKS, 3, "행운 III", 320000,"신이 도와주길 바랬다.","Mine", pickaxe)
        val e1 = enchantshopitem(p, 7, Enchantment.DIG_SPEED, 1, "효율 I",100000, "조금은 가벼워진 듯 하다.","Mine", pickaxe)
        val e2 = enchantshopitem(p, 14, Enchantment.DIG_SPEED, 2, "효율 II",240000, "한 손으로 들만하다.","Mine", pickaxe)
        val e3 = enchantshopitem(p, 26, Enchantment.DIG_SPEED, 3, "효율 III",380000, "종이 몇 장 수준이다.","Mine", pickaxe)
        val rp = GuiItem(
            ItemStack(Material.GRINDSTONE).apply {
                itemMeta = itemMeta.apply {
                    displayName(
                        Component.text("수리하기")
                            .decoration(TextDecoration.ITALIC,false)
                    )
                    lore(
                        listOf(
                            Component.text("클릭하여 수리 인벤토리를 엽니다.")
                                .color(TextColor.color(Main.colors(AQUA).asRGB()))
                                .decoration(TextDecoration.ITALIC,false)
                        )
                    )
                }
            }
        ) {
            val pl = it.whoClicked as Player
            pl.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW)
            //repair(pl).show(pl)
        }
        val chest = listOf(
            null, null, null, null, rp, null, null, null, null,
            p1, p2, p3, null, md, null, p4, p5, p6,
            u1, u2, u3, e1, e2, e3, l1, l2, l3,
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
        mineShop.addPane(mainPane)
        return mineShop
    }

    private fun fishgui(p: Player) : ChestGui {
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
        val gmi = shopitem(p,0,"낚시대",50000,"낚시의 기본.","Fish",Material.FISHING_ROD)
        val sl1 = enchantshopitem(p,3, Enchantment.LUCK,1,"행운 I",10000,"보물을 낚고 싶은가?","Fish",listOf(Material.FISHING_ROD))
        val sl2 = enchantshopitem(p,5, Enchantment.LUCK,2,"행운 II",24000,"욕심이 생기지 않는가?","Fish",listOf(Material.FISHING_ROD))
        val sl3 = enchantshopitem(p,7, Enchantment.LUCK,3,"행운 III",50000,"운의 끝을 보고 싶은가?","Fish",listOf(Material.FISHING_ROD))
        val ub1 = enchantshopitem(p,8, Enchantment.DURABILITY,1,"내구성 I",20000,"단단함의 기초.","Fish",listOf(Material.FISHING_ROD))
        val ub2 = enchantshopitem(p,10, Enchantment.DURABILITY,2,"내구성 II",44000,"견고함의 중점.","Fish",listOf(Material.FISHING_ROD))
        val ub3 = enchantshopitem(p,13, Enchantment.DURABILITY,3,"내구성 III",96000,"전문가의 손길.","Fish",listOf(Material.FISHING_ROD))
        val bt1 = enchantshopitem(p,15, Enchantment.LURE,1,"미끼 I",22000,"숙련가의 가호가 느껴진다.","Fish",listOf(Material.FISHING_ROD))
        val bt2 = enchantshopitem(p,17, Enchantment.LURE,2,"미끼 II",54000,"전문가의 기운이 느껴진다.","Fish",listOf(Material.FISHING_ROD))
        val bt3 = enchantshopitem(p,20, Enchantment.LURE,3,"미끼 III",120000,"장인이 함께하는 듯 하다.","Fish",listOf(Material.FISHING_ROD))
        val md = enchantshopitem(p,30, Enchantment.MENDING,1,"수선",500000,"낚시의 정점.","Fish",listOf(Material.FISHING_ROD))
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

    private fun farmgui(p: Player) : ChestGui {
        val farmShop = ChestGui(6, "농산품 상점")
        val test = p
        farmShop.title = p.customName.toString()
        return farmShop
    }

    @EventHandler
    fun tre(e: PlayerInteractEntityEvent) {
        val eh: EquipmentSlot = e.hand
        if (eh == EquipmentSlot.HAND) {
            if (e.rightClicked.name == "§b어부") {
                if (!e.player.isSneaking) {
                    e.player.msg(
                        """
                |§e어부 §7≫ §f안녕하세요!
                |§e어부 §7≫ §f팔 물건을 들고 좌클릭하세요.
                |§e어부 §7≫ §f웅크리고 있다면 한번에 팔 수 있어요. [보물 제외]
                |§e어부 §7≫ §f아, 낚시용품만 받아요. 그리고 물건을 사고 싶으면 웅크리고 불러주세요!""".trimMargin()
                    )
                } else {
                    val gui = fishgui(e.player)
                    gui.show(e.player)
                }
            } else if (e.rightClicked.name == "§7광부") {
                if (!e.player.isSneaking) {
                    e.player.msg(
                        """
                |§7광부 §7≫ §f안녕 친구!
                |§7광부 §7≫ §f팔 물건을 들고 좌클릭하라고.
                |§7광부 §7≫ §f웅크리고 있다면 한번에 팔 수 있다네.
                |§7광부 §7≫ §f광물만 받는다네. 그리고 물건을 사고 싶으면 웅크리고 불러주게!""".trimMargin()
                    )
                } else {
                    val gui = minegui(e.player)
                    gui.show(e.player)
                }
            } else if (e.rightClicked.name == "§a농부") {
                if (!e.player.isSneaking) {
                    e.player.msg(
                        """
                |§e농부 §7≫ §f여어!
                |§e농부 §7≫ §f팔 물건이 있으면 주라고.
                |§e농부 §7≫ §f웅크리고 있다면 한번에 받아 주지.
                |§e농부 §7≫ §f농산물만 줘. 웅크리고 우클릭해서 물건을 봐도 좋고!""".trimMargin()
                    )
                } else {
                    val gui = farmgui(e.player)
                    gui.show(e.player)
                }
            }
        }
    }

    @EventHandler
    fun tla(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.PLAYER && e.entity.name == "${AQUA}어부") {
            val p = e.damager as Player
            if (p.isSneaking){
                val i : Inventory = p.inventory
                var finalcost = 0
                i.forEach {
                    if (it != null) {
                        val mh: ItemStack = it
                        val mi = i.contents.indexOf(mh)
                        val amt = mh.amount
                        val cost: Int = when (mh.type) {
                            Material.COD -> 200
                            Material.SALMON -> 400
                            Material.TROPICAL_FISH -> 900
                            Material.PUFFERFISH -> 600
                            else -> 0
                        }
                        if (cost != 0) {
                            p.inventory.setItem(mi,ItemStack(Material.AIR))
                        }
                        finalcost += if (p.hasPermission("fisher.fish")) {
                            econ?.depositPlayer(p, (cost * amt * 1.05))
                            (cost * amt * 1.05).toInt()
                        } else {
                            econ?.depositPlayer(p, (cost * amt).toDouble())
                            (cost * amt)
                        }
                    }
                }
                if (finalcost == 0) {
                    p.msg("§e어부 §7≫ §f낚시용품만 주세요...")
                } else {
                    p.msg("§e어부 §7≫ §f${finalcost}원에 드리죠.")
                }
            } else {
                val amt = p.inventory.itemInMainHand.amount
                val cost: Int = when (p.inventory.itemInMainHand.type) {
                    Material.COD -> 200
                    Material.SALMON -> 400
                    Material.TROPICAL_FISH -> 900
                    Material.PUFFERFISH -> 600
                    else -> 0
                }
                val im = p.inventory.itemInMainHand.itemMeta
                if (cost == 0 && !(trash.itemMeta == im || treasure.itemMeta == im)) {
                    p.msg("§e어부 §7≫ §f낚시용품만 주세요...")
                } else if (!(trash.itemMeta == im || treasure.itemMeta == im)) {
                    p.equipment?.setItemInMainHand(ItemStack(Material.AIR))
                    if (p.hasPermission("fisher.fish")) {
                        econ?.depositPlayer(p, cost * amt * 1.05)
                        p.msg("§e어부 §7≫ §f${(cost * amt * 1.05).toInt()}원에 드리죠.")
                    } else {
                        econ?.depositPlayer(p, (cost * amt).toDouble())
                        p.msg("§e어부 §7≫ §f${cost * amt}원에 드리죠.")
                    }
                } else if (treasure.itemMeta == im) {
                    checkstreasure(p,p.inventory.itemInMainHand.amount)
                } else if (trash.itemMeta == im) {
                    checkstrash(p,p.inventory.itemInMainHand.amount)
                }
            }
        }
    }

    private fun checkstreasure(
        p: Player,
        amount: Int
    ) {
        p.inventory.setItemInMainHand(null)
        p.playSound(p.location,Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F)
        p.msg("§e어부 §7≫ §f아니 이건..?!")
        Timer().schedule(timerTask {
            p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
            p.msg("§e어부 §7≫ §f말로만 듣던 고대의 보물..!")
            Timer().schedule(timerTask {
                p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
                val scap = Math.random() * 100
                val value: Int = when {
                    scap >= 50 -> {
                        ((20000 + Math.random() * (30000-20000)) * amount).toInt()
                    }
                    scap >= 4 -> {
                        ((30000 + Math.random() * (50000-30000)) * amount).toInt()
                    }
                    scap >= 1 -> {
                        ((50000 + Math.random() * (75000-50000)) * amount).toInt()
                    }
                    scap >= 0.999 -> {
                        ((750000 + Math.random() * (1200000-750000)) * amount).toInt()
                    }
                    else -> {
                        ((75000 + Math.random() * (125000-75000)) * amount).toInt()
                    }
                }
                p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
                p.msg("§e어부 §7≫ §f${value}원에 살게요!")
                econ?.depositPlayer(p,value.toDouble())
            }, 2000L)
        }, 2000L)
    }

    private fun checkstrash(
        p: Player,
        amount: Int
    ) {
        p.inventory.setItemInMainHand(null)
        p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
        p.msg("§e어부 §7≫ §f이걸 지금..")
        Timer().schedule(timerTask {
            p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
            p.msg("§e어부 §7≫ §f받아달라는 건가요..?")
            Timer().schedule(timerTask {
                p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
                val scap = Math.random() * 100
                val value: Int = when {
                    scap >= 50 -> {
                        ((10 + Math.random() * (100-10)) * amount).toInt()
                    }
                    scap >= 4 -> {
                        ((100 + Math.random() * (250-100)) * amount).toInt()
                    }
                    scap >= 1 -> {
                        ((250 + Math.random() * (500-250)) * amount).toInt()
                    }
                    else -> {
                        ((500 + Math.random() * (1000-500)) * amount).toInt()
                    }
                }
                p.playSound(p.location,Sound.ENTITY_ITEM_PICKUP, 1F, 1F)
                p.msg("§e어부 §7≫ §f${value}원에 드리죠 뭐.")
                econ?.depositPlayer(p,value.toDouble())
            }, 2000L)
        }, 2000L)
    }
}