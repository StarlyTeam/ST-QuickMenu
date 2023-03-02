package net.starly.qm.data

import net.starly.qm.context.DELAY_PROC_TICK
import net.starly.qm.context.RIGHT_CLICK_PROC_TICK
import net.starly.qm.context.TIME_OUT_TICK
import net.starly.qm.enum.PlayerState
import org.bukkit.Location

class PlayerStateData(
    val startLocation: Location
) {

    var state: PlayerState = PlayerState.RIGHT_CLICK
        private set
    private var proc: Int = RIGHT_CLICK_PROC_TICK
    private var maintain: Int = 0

    fun timeOut() = maintain >= TIME_OUT_TICK

    fun delay() {
        proc = DELAY_PROC_TICK
        state = PlayerState.DELAY
    }

    fun moveOtherBlock() {
        state = PlayerState.MOVE_BLOCK
    }

    fun click() { proc = 0 }

    fun process(maintain: Boolean = true): Boolean {
        if(maintain) this.maintain++
        return --proc > 0
    }

}