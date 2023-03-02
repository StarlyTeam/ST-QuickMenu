package net.starly.qm.listener

import net.starly.qm.QuickMenu
import net.starly.qm.enum.PlayerState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class PlayerMoveListener(
    plugin: QuickMenu
): Listener {

    private val stateRepo = plugin.playerDataRepository

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        if(stateRepo.contains(player.uniqueId)) {
            val data = stateRepo.get(player.uniqueId)
            if(data?.state == PlayerState.RIGHT_CLICK) {
                if (!(event.to.blockX == event.from.blockX && event.to.blockY == event.from.blockY && event.to.blockZ == event.from.blockZ))
                    data.moveOtherBlock()
            }
        }
    }

}