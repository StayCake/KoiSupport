package com.koisv.support.tools

import com.koisv.customenchants.enchants.RangeHarvest
import com.koisv.customenchants.enchants.RangeSoil
import com.koisv.support.Main
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.reflect.KClass

class Instance {
    companion object {
        private fun getInstance(): Plugin {
            return Main.instance
        }

        private fun getStats(): YamlConfiguration {
            return Main.stats
        }

        private fun getStatsloc(): File {
            return Main.statsloc
        }

        private fun getShop(): YamlConfiguration {
            return Main.shop
        }

        val config = getInstance().config

        val stat = getStats()

        val statloc = getStatsloc()

        val customshop = getShop()

        val rangeSoil = RangeSoil()

        val rangeHarvest = RangeHarvest()
    }
}