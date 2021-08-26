package com.koisv.support.misc.tools

import com.koisv.customenchants.enchants.RangeHarvest
import com.koisv.customenchants.enchants.RangeSoil
import com.koisv.support.Main
import hazae41.minecraft.kutils.get
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

class Instance {
    companion object {
        fun init(dataFolder: File) {
            Main.instance.saveDefaultConfig()
            if (!dataFolder["data"]["shop.yml"].canRead()) {
                dataFolder["data"].mkdir()
                Main.shop.save(dataFolder["data"]["shop.yml"])
            }
            if (!dataFolder["data"]["stats.yml"].canRead()) {
                dataFolder["data"].mkdir()
                Main.stats.save(Main.statsLoc)
            }
        }

        private fun getInstance(): Plugin {
            return Main.instance
        }

        private fun getStats(): YamlConfiguration {
            return Main.stats
        }

        private fun getStatsLoc(): File {
            return Main.statsLoc
        }

        private fun getShop(): YamlConfiguration {
            return Main.shop
        }

        val config = getInstance().config

        val stat = getStats()

        val statLoc = getStatsLoc()

        val customShop = getShop()

        val rangeSoil = RangeSoil()

        val rangeHarvest = RangeHarvest()
    }
}