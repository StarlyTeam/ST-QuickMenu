package net.starly.qm.setting.impl.message

import net.starly.qm.extension.toColoredString
import net.starly.qm.setting.Setting
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

class MessageSetting : Setting {

    override val sectionKey: String = "message"

    private val messageMap = HashMap<String, String>()

    override fun load(config: ConfigurationSection) {
        val section = config.getConfigurationSection(sectionKey)?: return
        section.getKeys(false).forEach {
            messageMap[it] = section.getString(it).toColoredString()
        }
    }

    fun sendMessage(target: Player, key: String, prefix: Boolean = true) {
        val builder = StringBuilder()
        if(prefix) builder.append(messageMap["prefix"]?: "")
        builder.append(messageMap[key]?: "")
        val str = builder.toString()
        if(str.isEmpty()) return
        target.sendMessage(str)
    }

    fun get(key: String): String {
        return messageMap[key]?: ""
    }

    fun getPrefix(): String {
        return messageMap["prefix"]?: ""
    }

    override fun isInvalid(): Boolean {
        return messageMap.isNotEmpty()
    }

}