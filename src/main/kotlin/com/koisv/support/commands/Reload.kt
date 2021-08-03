package com.koisv.support.commands

import com.koisv.support.Main
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.Sound
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

    private fun getStatsloc(): File {
        return Main.statsloc
    }

    fun register(builder: LiteralNode) {
        builder.requires {
            hasPermission(4,"admin.reload") && playerOrNull != null
        }
        builder.executes {
            val p = sender as Player
            getInstance().reloadConfig()
            getStats().load(getStatsloc())
            p.sendMessage("리로드 완료!")
            p.playSound(p.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F)
        }
    }
}