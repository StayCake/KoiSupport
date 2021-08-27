package com.koisv.support

import com.koisv.support.commands.Game
import com.koisv.support.commands.Menu
import com.koisv.support.commands.Reload
import com.koisv.support.commands.StatManage
import com.koisv.support.misc.tools.Instance
import hazae41.minecraft.kutils.bukkit.info
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.kommand
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.time.LocalTime
import java.util.*

var econ: Economy? = null
var chat: Chat? = null

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set
        lateinit var stats: YamlConfiguration
            private set
        lateinit var statsLoc: File
            private set
        lateinit var shop: YamlConfiguration
            private set
        var woodNow: MutableList<Block> = mutableListOf()
        var woodOwner: MutableMap<Block, Player> = mutableMapOf()
        var woodDamage: MutableMap<Block, Int> = mutableMapOf()
        var woodTime: MutableMap<Block, LocalTime?> = mutableMapOf()
        var placeCheck: MutableList<Block> = mutableListOf()
        fun colors(cc: ChatColor): Color {
            return when (cc) {
                ChatColor.AQUA -> Color.AQUA
                ChatColor.BLACK -> Color.BLACK
                ChatColor.BLUE -> Color.BLUE
                ChatColor.DARK_AQUA -> Color.TEAL
                ChatColor.DARK_BLUE -> Color.NAVY
                ChatColor.DARK_GRAY -> Color.GRAY
                ChatColor.DARK_GREEN -> Color.fromRGB(0, 65, 0)
                ChatColor.DARK_PURPLE -> Color.PURPLE
                ChatColor.DARK_RED -> Color.MAROON
                ChatColor.GOLD -> Color.fromRGB(218, 165, 32)
                ChatColor.GRAY -> Color.SILVER
                ChatColor.GREEN -> Color.LIME
                ChatColor.LIGHT_PURPLE -> Color.FUCHSIA
                ChatColor.RED -> Color.RED
                ChatColor.WHITE -> Color.WHITE
                ChatColor.YELLOW -> Color.YELLOW
                else -> Color.WHITE
            }
        }
    }

    private fun setupVault(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rspE: RegisteredServiceProvider<Economy> = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return true
        val rspC: RegisteredServiceProvider<Chat> = server.servicesManager.getRegistration(
            Chat::class.java
        ) ?: return true
        econ = rspE.provider
        chat = rspC.provider
        return chat != null && econ != null
    }

    override fun onEnable() {
        fun getCC() : Plugin? {
            return Bukkit.getPluginManager().getPlugin("CustomEnchants")
        }
        if (!setupVault() && getCC() == null) {
            if (!setupVault()) info("Vault가 감지되지 않았습니다!")
            if (getCC() == null) info("CustomEnchants가 감지되지 않았습니다!")
            server.pluginManager.disablePlugin(this)
            return
        }

        info("가동 시작!")

        instance = this
        Events(this)

        statsLoc = dataFolder["data"]["stats.yml"]
        stats = YamlConfiguration.loadConfiguration(statsLoc)
        shop = YamlConfiguration.loadConfiguration(dataFolder["data"]["shop.yml"])
        Instance.init(dataFolder)

        kommand {
            register("ks") {
                Reload.register(this)
            }
            register("menu","메뉴") {
                Menu.register(this)
            }
            register("스텟관리") {
                StatManage.register(this)
            }
            register("미니게임") {
                Game.register(this)
            }
        }
    }

    override fun onDisable() {
        saveConfig()
        info("가동 중지.")
    }
}