package net.starly.qm.listener

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.setting.impl.DefaultSetting
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class QBIconHandleListener: Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSwap(event: PlayerSwapHandItemsEvent) {
        if(event.isCancelled) return
        if(isIcon(event.player.inventory.itemInMainHand))
            event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onSwap(event: PlayerDropItemEvent) {
        if(event.isCancelled) return
        if(isIcon(event.itemDrop.itemStack))
            event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onClick(event: InventoryClickEvent) {
        if(event.isCancelled) return
        if(isIcon(event.currentItem)) event.isCancelled = true
        if(event.hotbarButton >= 0)
            if(isIcon(event.whoClicked.inventory.getItem(event.hotbarButton)))
                event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.player.inventory.itemInMainHand
        if(isIcon(item)) {
            val cfg = ConfigLoader.get(null, DefaultSetting::class.java)
            try {
                if (!cfg.icon.isInvalid())
                    if (cfg.icon.slot - 1 != event.player.inventory.heldItemSlot)
                        item.amount = 0
                else if(!cfg.isOpenType(DefaultSetting.OpenType.ICON)) item.amount = 0
            } catch (_: Exception) {}
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onJoin(event: PlayerJoinEvent) {
        clearingIcon(event.player)
        val cfg = ConfigLoader.get(null, DefaultSetting::class.java)
        if(cfg.isOpenType(DefaultSetting.OpenType.ICON))
            event.player.inventory.setItem(cfg.icon.slot - 1, cfg.icon.headIcon.clone())
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player: Player = event.entity
        val inventory: Array<ItemStack> = player.inventory.contents
        val fifthSlotItem: ItemStack = inventory[4]
        val cfg = ConfigLoader.get(null, DefaultSetting::class.java)

        try {
            if (fifthSlotItem.type != Material.AIR && cfg.isOpenType(DefaultSetting.OpenType.ICON)) {
                event.drops.remove(fifthSlotItem)
                inventory[4] = ItemStack(Material.AIR)
                player.inventory.contents = inventory
            }
        } catch (ignored: Exception) {}
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onRespawn(event: PlayerRespawnEvent) {
        clearingIcon(event.player)
        val cfg = ConfigLoader.get(null, DefaultSetting::class.java)
        if (cfg.isOpenType(DefaultSetting.OpenType.ICON)){
            event.player.inventory.setItem(cfg.icon.slot -1, cfg.icon.headIcon.clone())
        }
    }

    companion object {
        fun clearingIcon(player: Player) {
            player.inventory.contents.filter {
                if(it != null && it.type != Material.AIR) {
                    val nms = NmsItemStackUtil.getInstance()?.asNMSCopy(it)
                    val tag = nms?.tag
                    tag?.getString("ST-QuickButton") == "Icon"
                } else false
            }.forEach { it.amount = 0 }
        }
        fun isIcon(item: ItemStack?): Boolean {
            if (item == null || item.type == Material.AIR) return false
            val nms = NmsItemStackUtil.getInstance()?.asNMSCopy(item) ?: return false
            val tag = nms.tag ?: return false
            val str = tag.getString("ST-QuickButton") ?: return false
            return str == "Icon"
        }
    }

}