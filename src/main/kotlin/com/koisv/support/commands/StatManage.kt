package com.koisv.support.commands

import com.koisv.support.Main
import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object StatManage {

    private fun getStats(): YamlConfiguration {
        return Main.stats
    }

    private fun getStatsLoc(): File {
        return Main.statsLoc
    }
    private val statData = getStats()

    fun register(builder: LiteralNode) {
        builder.requires { playerOrNull != null && hasPermission(4,"admin.stats") }
        builder.then("설정") {
            then("user" to KommandArgument.player()) {
                then("data" to KommandArgument.string()) {
                    then("value" to KommandArgument.int()) {
                        executes { kr ->
                            val user: Player by kr
                            val uuid : String = user.uniqueId.toString()
                            val data : String by kr
                            val value : Int by kr
                            if (statData.contains("${uuid}.$data")) {
                                statData.set("${uuid}.$data", value)
                                statData.save(getStatsLoc())
                                sender.msg("$data -> $value 완료.")
                            } else {
                                statData.set("${uuid}.$data", value)
                                statData.save(getStatsLoc())
                                sender.msg("$data -> $value 완료.")
                            }
                        }
                    }
                }
            }
        }
        builder.then("확인") {
            then("user" to KommandArgument.player()) {
                then("data" to KommandArgument.string()) {
                    executes { kr ->
                        val user: Player by kr
                        val uuid : String = user.uniqueId.toString()
                        if (statData.contains(uuid)) {
                            val data: String by kr
                            val d = statData.getInt("${uuid}.$data")
                            sender.msg("$data = $d.")
                        } else {
                            sender.msg("유저 값이 존재하지 않습니다.")
                        }
                    }
                }
            }
        }
        builder.then("삭제") {
            then("user" to KommandArgument.player()) {
                then("data" to KommandArgument.string()) {
                    executes { kr ->
                        val user: Player by kr
                        val uuid : String = user.uniqueId.toString()
                        if (statData.contains(uuid)) {
                            val data: String by kr
                            statData.set("${uuid}.$data", null)
                            statData.save(getStatsLoc())
                            sender.msg("$data 삭제.")
                        } else {
                            sender.msg("유저 값이 존재하지 않습니다.")
                        }
                    }
                }
            }
        }
    }
}