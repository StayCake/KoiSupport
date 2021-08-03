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

    private fun getStatsloc(): File {
        return Main.statsloc
    }
    private val statdata = getStats()

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
                            if (statdata.contains("${uuid}.$data")) {
                                statdata.set("${uuid}.$data", value)
                                statdata.save(getStatsloc())
                                sender.msg("$data -> $value 완료.")
                            } else {
                                statdata.set("${uuid}.$data", value)
                                statdata.save(getStatsloc())
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
                        if (statdata.contains(uuid)) {
                            val data: String by kr
                            val d = statdata.getInt("${uuid}.$data")
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
                        if (statdata.contains(uuid)) {
                            val data: String by kr
                            statdata.set("${uuid}.$data", null)
                            statdata.save(getStatsloc())
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