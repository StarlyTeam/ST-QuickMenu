package net.starly.qm.extension

import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.setting.impl.message.MessageSetting
import org.bukkit.entity.Player

internal fun Player.sendMessageAfterPrefix(msg: String) =
    sendMessage(ConfigLoader.get(null, MessageSetting::class.java).getPrefix() + msg)