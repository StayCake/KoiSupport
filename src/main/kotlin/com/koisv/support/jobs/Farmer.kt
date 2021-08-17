package com.koisv.support.jobs

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.koisv.customenchants.Utils.Misc.Companion.hoe
import com.koisv.support.tools.Instance.Companion.rangeHarvest
import com.koisv.support.tools.Instance.Companion.rangeSoil
import com.koisv.support.tools.Shops
import org.bukkit.entity.Player
import com.koisv.support.tools.Shops.Companion.shopItem
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment

class Farmer {
    companion object {
        fun farmgui(p: Player) : ChestGui {
            val farmShop = ChestGui(3, "농산품 상점")
            val farmPane = StaticPane(9,3)
            val rs1 = shopItem(p,4, rangeSoil.key,1,15000,"더 넓은 경작을 도와줍니다. [3x3]","Farm")
            val rs2 = shopItem(p,10, rangeSoil.key,2,32000,"더 넓은 경작을 도와줍니다. [5x5]","Farm")
            val rs3 = shopItem(p,18, rangeSoil.key,3,70000,"더 넓은 경작을 도와줍니다. [7x7]","Farm")
            val ub1 = shopItem(p,8, Enchantment.DURABILITY,1,"내구성 I",6000,"단단함의 기초.","Farm",hoe)
            val ub2 = shopItem(p,10, Enchantment.DURABILITY,2,"내구성 II",15000,"견고함의 중점.","Farm",hoe)
            val ub3 = shopItem(p,13, Enchantment.DURABILITY,3,"내구성 III",32000,"전문가의 손길.","Farm",hoe)
            val rh1 = shopItem(p,7, rangeHarvest.key,1,16000,"더 넓은 경작을 도와줍니다.","Farm")
            val rh2 = shopItem(p,13, rangeHarvest.key,2,34000,"더 넓은 경작을 도와줍니다.","Farm")
            val rh3 = shopItem(p,20, rangeHarvest.key,3,76000,"더 넓은 경작을 도와줍니다.","Farm")

            val farmChest = listOf(
                rs1,rs2,rs3,ub1,ub2,ub3,rh1,rh2,rh3,
                null
            )
            var idx = 0
            var line = 0
            farmChest.forEach {
                if (idx == 9) {
                    idx = 0
                    line++
                }
                if (it != null) farmPane.addItem(it,idx,line)
                idx++
            }
            farmShop.addPane(farmPane)
            farmShop.title = p.customName.toString()
            return farmShop
        }
    }
}