package net.starly.qm.listener.unregist

import net.starly.qm.QuickMenu
import net.starly.qm.QuickMenuSetter
import net.starly.qm.listener.IconHandleListener
import net.starly.qm.listener.UnregisterListener
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class RightClickListener(
    private val plugin: QuickMenu
): UnregisterListener {

    private val stateRepo = plugin.playerDataRepository

    override fun unregister() {
        PlayerInteractEvent.getHandlerList().unregister(this)
    }

    @EventHandler fun onInteract(event: PlayerInteractEvent) =
        action(event.player, event.hand, event, event.action)

    private fun action(player: Player, hand: EquipmentSlot?, cancellable: Cancellable, action: Action) {
        if(hand != EquipmentSlot.HAND || action == Action.PHYSICAL) return
        val item = player.inventory.itemInMainHand
        if(!IconHandleListener.isIcon(item)) return

        cancellable.isCancelled = true
        if(stateRepo.contains(player.uniqueId)) return

        if(!(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) return
        QuickMenuSetter.start(player, stateRepo, plugin)
    }

}