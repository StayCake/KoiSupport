package com.koisv.support.commands

import com.koisv.support.misc.ui.MoneyUI
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.entity.Player

object Menu {
    fun register(builder: LiteralNode) {
        builder.requires { playerOrNull != null }
        builder.executes {
            MoneyUI.mainUi(this).show(sender as Player)
        }
    }
}