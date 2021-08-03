package com.koisv.support

import com.koisv.support.commands.Game
import com.koisv.support.commands.Menu
import com.koisv.support.commands.Reload
import com.koisv.support.commands.StatManage
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.kommand
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

var econ: Economy? = null
var chat: Chat? = null

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set
        lateinit var stats: YamlConfiguration
            private set
        lateinit var statsloc: File
            private set
        lateinit var colors: (ChatColor) -> Color
            private set
    }

    private fun setupEconomy(): Boolean {
        if (server.pluginManager.getPlugin("Vault") == null) {
            return false
        }
        val rsp: RegisteredServiceProvider<Economy> = server.servicesManager.getRegistration(
            Economy::class.java
        ) ?: return true
        econ = rsp.provider
        return econ != null
    }

    private fun setupChat(): Boolean {
        val rsp: RegisteredServiceProvider<Chat> = server.servicesManager.getRegistration(
            Chat::class.java
        ) ?: return true
        chat = rsp.provider
        return chat != null
    }

    override fun onEnable() {
        colors = fun(cc: ChatColor): Color {
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

        if (!setupEconomy()) {
            println(String.format("[%s] - Vault가 감지되지 않았습니다!", description.name))
            server.pluginManager.disablePlugin(this)
            return
        }
        setupChat()
        println(String.format("[%s] - 가동 시작!", description.name))

        instance = this

        server.pluginManager.registerEvents(Events(), this)
        saveDefaultConfig()

        stats = YamlConfiguration.loadConfiguration(dataFolder["data"]["stats.yml"])
        statsloc = dataFolder["data"]["stats.yml"]

        if (!dataFolder["data"]["stats.yml"].canRead()) {
            dataFolder["data"].mkdir()
            stats.save(statsloc)
        }
        kommand {
            register("test") {
                //requires { playerOrNull != null }
                then("test") {
                    executes { println("야호") }
                }
            }
            register("ksreload") {
                Reload.register(this)
            }
            register("메뉴") {
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
        println(String.format("[%s] - 가동 중지.", description.name))
    }
}