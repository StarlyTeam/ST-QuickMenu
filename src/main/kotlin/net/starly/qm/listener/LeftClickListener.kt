package net.starly.qm.listener

import net.starly.qm.QuickMenu
import net.starly.qm.enum.PlayerState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class LeftClickListener(
    plugin: QuickMenu,
) : Listener {

    private val stateRepo = plugin.playerDataRepository

    @EventHandler
    fun onLeftClick(event: PlayerInteractEvent) {
        val player = event.player
        if (stateRepo.contains(player.uniqueId)) {
            event.isCancelled = true
            if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
                val temp = stateRepo.get(player.uniqueId)?: return
                if(temp.state == PlayerState.RIGHT_CLICK) temp.click()
            }
        }
    }
}