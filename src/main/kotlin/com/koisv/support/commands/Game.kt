package com.koisv.support.commands

import com.koisv.support.ui.GameUI
import io.github.monun.kommand.node.LiteralNode

object Game {
    fun register(Builder: LiteralNode) {
        Builder.requires { playerOrNull != null }
        Builder.executes {
            GameUI.execute(player) {
                GameUI.game(it)
            }
        }
    }
}