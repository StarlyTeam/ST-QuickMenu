package net.starly.qm.runnable

import net.starly.qm.data.PlayerStateData
import net.starly.qm.enum.CallBackReason
import net.starly.qm.enum.PlayerState
import net.starly.qm.repo.DataRepository
import org.bukkit.entity.Player
import java.util.UUID

class RightClickCallBackRunnable(
    private val repo: DataRepository<UUID, PlayerStateData>,
    private val player: Player,
    private val slot: Int,
    override val callbackFunction: (CallBackReason)->Unit
): CallbackRunnable<CallBackReason>() {


    override fun runBlock() {
        if(!player.isOnline) return cancel(CallBackReason.OFFLINE)
        //if(player.inventory.heldItemSlot != slot) return cancel(CallBackReason.CHANGE_SLOT)
        val state = repo.get(player.uniqueId)?: return cancel(CallBackReason.NULL_DATA)
        if(state.state == PlayerState.MOVE_BLOCK ||state.startLocation.distance(player.location) >= 9.0) return cancel(CallBackReason.RUN_AWAY)
        if(state.process()) {
            if(state.timeOut()) return cancel(CallBackReason.TIME_OUT)
        } else {
            if(state.timeOut()) return cancel(CallBackReason.TIME_OUT)
            return cancel(CallBackReason.CLICK_OFF)
        }
    }

}