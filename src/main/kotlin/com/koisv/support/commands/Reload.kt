package com.koisv.support.commands

import com.koisv.support.Main
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.node.LiteralNode
import net.kyori.adventure.key.Key
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.File

object Reload {

    private fun getInstance(): Plugin {
        return Main.instance
    }

    private fun getStats(): YamlConfiguration {
        return Main.stats
    }

    private fun getStatsLoc(): File {
        return Main.statsLoc
    }

    fun register(builder: LiteralNode) {
        builder.then("reload") {
            requires {
                hasPermission(4,"admin.reload") && playerOrNull != null
            }
            executes {
                val p = sender as Player
                getInstance().config.load(getInstance().dataFolder["config.yml"])
                getStats().load(getStatsLoc())
                Main.shop.load(getInstance().dataFolder["data"]["shop.yml"])
                p.sendMessage("리로드 완료!")
                p.playSound(
                    net.kyori.adventure.sound.Sound.sound(
                        Key.key("entity.player.levelup"),
                        net.kyori.adventure.sound.Sound.Source.PLAYER,
                        1F,
                        1F
                    )
                )
            }
        }
    }
}