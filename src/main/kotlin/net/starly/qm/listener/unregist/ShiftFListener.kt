package net.starly.qm.listener.unregist

import net.starly.qm.QuickMenu
import net.starly.qm.QuickMenuSetter
import net.starly.qm.listener.UnregisterListener
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class ShiftFListener(
    private val plugin: QuickMenu,
) : UnregisterListener {

    private val stateRepo = plugin.playerDataRepository

    override fun unregister() {
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onSwapItemHand(event: PlayerSwapHandItemsEvent) {
        if (stateRepo.contains(event.player.uniqueId)) return
        else if (event.player.isSneaking) {
            event.isCancelled = true
            QuickMenuSetter.start(event.player, stateRepo, plugin)
        }
    }

}