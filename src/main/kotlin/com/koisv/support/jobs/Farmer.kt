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
        fun farmGui(p: Player) : ChestGui {
            val farmShop = ChestGui(3, "농산품 상점")
            val farmPane = StaticPane(9,3)
            val rs1 = shopItem(p,4, rangeSoil.key,1,15000,"더 넓은 경작을 도와줍니다. [3x3]","Farm")
            val rs2 = shopItem(p,10, rangeSoil.key,2,32000,"더 넓은 경작을 도와줍니다. [5x5]","Farm")
            val rs3 = shopItem(p,18, rangeSoil.key,3,70000,"더 넓은 경작을 도와줍니다. [7x7]","Farm")
            val ft1 = shopItem(p, 5, Enchantment.LOOT_BONUS_BLOCKS, 1, "행운 I", 25000,"네잎 클로버를 찾았다.","Farm",hoe)
            val ft2 = shopItem(p, 12, Enchantment.LOOT_BONUS_BLOCKS, 2, "행운 II", 60000,"행운이 함께하길 빌었다.","Farm",hoe)
            val ft3 = shopItem(p, 21, Enchantment.LOOT_BONUS_BLOCKS, 3, "행운 III", 150000,"신이 도와주길 바랬다.","Farm",hoe)
            val ub1 = shopItem(p,8, Enchantment.DURABILITY,1,"내구성 I",6000,"단단함의 기초.","Farm",hoe)
            val ub2 = shopItem(p,10, Enchantment.DURABILITY,2,"내구성 II",15000,"견고함의 중점.","Farm",hoe)
            val ub3 = shopItem(p,13, Enchantment.DURABILITY,3,"내구성 III",32000,"전문가의 손길.","Farm",hoe)
            val rh1 = shopItem(p,7, rangeHarvest.key,1,16000,"더 넓은 경작을 도와줍니다.","Farm")
            val rh2 = shopItem(p,13, rangeHarvest.key,2,34000,"더 넓은 경작을 도와줍니다.","Farm")
            val rh3 = shopItem(p,20, rangeHarvest.key,3,76000,"더 넓은 경작을 도와줍니다.","Farm")
            val sd1 = shopItem(p, 0, null, 200, "다 자라면 밀이 되는 씨앗.", "Farm", Material.WHEAT_SEEDS,null)
            val sd2 = shopItem(p, 0, null, 200, "다 자라면 사탕무가 되는 씨앗.", "Farm", Material.BEETROOT_SEEDS,null)
            val sd3 = shopItem(p, 0, null, 200, "다 자라면 수박이 열리는 씨앗.", "Farm", Material.MELON_SEEDS,null)
            val sd4 = shopItem(p, 0, null, 200, "다 자라면 호박이 열리는 씨앗.", "Farm", Material.PUMPKIN_SEEDS,null)
            val sd5 = shopItem(p, 0, null, 200, "누가 흘리고 간 코코아 콩이다.", "Farm", Material.COCOA_BEANS,null)
            val sd6 = shopItem(p, 0, null, 200, "맛있는 감자.", "Farm", Material.POTATO,null)
            val sd7 = shopItem(p, 0, null, 200, "먹기 딱 좋은 크기의 당근이다.", "Farm", Material.CARROT,null)
            val sd8 = shopItem(p, 0, null, 200, "사탕수수. 원래 이거 단단한 녀석 아니었나?", "Farm", Material.SUGAR_CANE,null)
            val sd9 = shopItem(p, 0, null, 200, "달콤한 만큼 당신을 찔러주는 열매다.", "Farm", Material.SWEET_BERRIES,null)
            val ho1 = shopItem(p, 0, null,60000,"금으로 도구를 만든 나머지 너무 약해져 버렸다...","Farm",Material.GOLDEN_HOE)
            val ho2 = shopItem(p, 5, null,130000,"적어도 나무는 금보다 나을 것이다.","Farm",Material.WOODEN_HOE)
            val ho3 = shopItem(p, 12, null,270000,"돌멩이 몇개 주워다 갈아 보았다.","Farm",Material.STONE_HOE)
            val ho4 = shopItem(p, 19, null,580000,"옆집 대장장이 아재가 만들어 주었다.","Farm",Material.IRON_HOE)
            val ho5 = shopItem(p, 26, null,1200000,"보석상이 자신만만하게 만들어 온 녀석이다.","Farm",Material.DIAMOND_HOE)
            val ho6 = shopItem(p, 32, null,2500000,"도를 넘은 전념.","Farm",Material.NETHERITE_HOE)

            val farmChest = listOf(
                ub1,ub2,ub3,ho1,ho2,ho3,ft1,ft2,ft3,
                rs1,rs2,rs3,ho4,ho5,ho6,rh1,rh2,rh3,
                sd1,sd2,sd3,sd4,sd5,sd6,sd7,sd8,sd9,
            )
            var idx = 0
            var line = 0
            farmChest.forEach {
                if (idx == 9) {
                    idx = 0
                    line++
                }
                farmPane.addItem(it,idx,line)
                idx++
            }
            farmShop.addPane(farmPane)
            return farmShop
        }

        fun getCropCost(type: Material) : Int {
            return when (type) {
                Material.WHEAT -> 750
                Material.WHEAT_SEEDS -> 25
                Material.BEETROOT -> 300
                Material.BEETROOT_SEEDS -> 25
                Material.MELON_SLICE -> 75
                Material.PUMPKIN -> 400
                Material.COCOA_BEANS -> 175
                Material.POTATO -> 150
                Material.CARROT -> 150
                Material.SUGAR_CANE -> 175
                Material.SWEET_BERRIES -> 175
                else -> 0
            }
        }
    }
}