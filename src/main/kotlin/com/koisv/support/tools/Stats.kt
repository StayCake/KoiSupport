package com.koisv.support.tools

import com.koisv.support.tools.Instance.Companion.config
import com.koisv.support.tools.Instance.Companion.rangeHarvest
import com.koisv.support.tools.Instance.Companion.rangeSoil
import hazae41.minecraft.kutils.bukkit.msg
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

class Stats {
    companion object {

        fun convert(type: String, target: Player, price: Int) : List<String>{
            return when (type){
                "Fish" -> {
                    listOf(if (target.hasPermission("fisher.fish")) "${(price * 0.95).toInt()}원 [어부 할인가]" else "${price}원","§e어부","낚시")
                }
                "Farm" -> {
                    listOf(if (target.hasPermission("farmer.farm")) "${(price * 0.95).toInt()}원 [농부 할인가]" else "${price}원","§a농부","농사")
                }
                "Mine" -> {
                    listOf(if (target.hasPermission("miner.mine")) "${(price * 0.95).toInt()}원 [광부 할인가]" else "${price}원","§b광부","채광")
                }
                "shop" -> {
                    listOf(if (target.hasPermission("admin.sale")) "${(price * 0.95).toInt()}원 [상점 할인가]" else "${price}원","§1상인","")
                }
                else -> {
                    listOf("0","null","null")
                }
            }
        }

        fun convert(type: String) : String {
            return when (type) {
                "Fish" -> "fisher"
                "Farm" -> "farmer"
                "Mine" -> "miner"
                else -> "null"
            }
        }

        fun setStat(
            d: Int,
            p: Player,
            t: String
        ) {
            if (Instance.stat.isInt("${p.uniqueId}.${t}Exp")){
                val prd = Instance.stat.getInt("${p.uniqueId}.${t}Exp")
                expcalc(d + prd,Instance.stat.getInt("${p.uniqueId}.${t}LV"), p, convert(t))
                Instance.stat.set("${p.uniqueId}.${t}Exp",d + prd)
                Instance.stat.save(Instance.statloc)
            } else {
                Instance.stat.set("${p.uniqueId}.${t}LV",1)
                Instance.stat.set("${p.uniqueId}.${t}Exp",d)
                Instance.stat.save(Instance.statloc)
            }
        }

        fun getStat(
            p: Player,
            t: String
        ): Int {
            return if (Instance.stat.isInt("${p.uniqueId}.${t}LV")){
                Instance.stat.getInt("${p.uniqueId}.${t}LV")
            } else {
                0
            }
        }

        fun showStat(p : Player, t: String){
            val lv = getStat(p,t)
            val type = convert(t)
            val first = config.getInt("values.firstexp.$type")
            val term = config.getInt("values.expterm.$type")
            val max = lv * ( first + ( first + ( term * (lv-1) ) ) )/ 2
            val exp = if (Instance.stat.isInt("${p.uniqueId}.${t}Exp")){
                Instance.stat.getInt("${p.uniqueId}.${t}Exp")
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
                    Sound.sound(
                        Key.key("entity.player.levelup"),
                        Sound.Source.PLAYER,
                        1F,
                        1F
                    )
                )
                val ts = when (type) {
                    "fisher" -> "Fish"
                    "farmer" -> "Farm"
                    "miner" -> "Mine"
                    else -> ""
                }
                Instance.stat.set("${t.uniqueId}.${ts}LV",(cl + ul))
                Instance.stat.save(Instance.statloc)
            }
        }
    }
}