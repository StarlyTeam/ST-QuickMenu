package net.starly.qm.setting

import org.bukkit.configuration.ConfigurationSection

interface Setting {
    val sectionKey: String
    fun load(config: ConfigurationSection)
    fun isInvalid(): Boolean
}