package net.starly.qm.listener

import org.bukkit.event.Listener

interface UnregisterListener: Listener {
    fun unregister()
}