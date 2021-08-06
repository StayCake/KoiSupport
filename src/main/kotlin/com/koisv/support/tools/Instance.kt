package com.koisv.support.tools

import com.koisv.support.Main
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

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

        val config = getInstance().config

        val stat = getStats()

        val statloc = getStatsloc()
    }
}