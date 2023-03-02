package net.starly.qm.loader.impl

import net.starly.qm.QuickMenu
import net.starly.qm.listener.QBIconHandleListener
import net.starly.qm.listener.unregist.QBRightClickListener
import net.starly.qm.listener.unregist.QBShiftFListener
import net.starly.qm.listener.UnregisterListener
import net.starly.qm.setting.Setting
import net.starly.qm.loader.Loader
import net.starly.qm.setting.impl.DefaultSetting
import net.starly.qm.setting.impl.message.MessageSetting
import org.bukkit.event.Listener
import java.io.File

object ConfigLoader: Loader<Setting> {

    private val settings = ArrayList<Setting>()
    private val listeners = ArrayList<Listener>()

    override fun load(plugin: QuickMenu) {
        plugin.server.onlinePlayers.forEach(QBIconHandleListener::clearingIcon)
        settings.clear()
        listeners.filterIsInstance<UnregisterListener>().forEach(UnregisterListener::unregister)
        listeners.clear()
        val file = File(plugin.dataFolder, "config.yml")
        if(!file.exists()) plugin.saveDefaultConfig()
        else plugin.reloadConfig()
        val config = plugin.config
        settings.add(MessageSetting().apply { load(config) })
        settings.add(DefaultSetting().apply { load(config) })
        get(null, DefaultSetting::class.java).apply {
            plugin.serverPreset = plugin.presetDateRepository.get(preset)
            openTypeList.forEach {
                when(it) {
                    DefaultSetting.OpenType.COMMAND-> {}
                    DefaultSetting.OpenType.ICON-> {
                        listeners.add(QBRightClickListener(plugin))
                        plugin.server.onlinePlayers.forEach { player -> player.inventory.setItem(icon.slot - 1, icon.headIcon.clone()) }
                    }
                    DefaultSetting.OpenType.SHIFT_F-> listeners.add(QBShiftFListener(plugin))
                }
            }
        }
        listeners.forEach { plugin.server.pluginManager.registerEvents(it, plugin) }
        plugin.serverPreset = plugin.presetDateRepository.get(get(null, DefaultSetting::class.java).preset)
    }

    @Suppress("Unchecked_cast")
    override fun <T: Setting> get(obj: T?, clazz: Class<T>): T {
        return settings.filterIsInstance(clazz).first()
    }

}