package net.starly.qm.extension

import org.bukkit.ChatColor

internal fun String.toColoredString(): String =
    ChatColor.translateAlternateColorCodes('&', this)?: this

internal fun String.formattedString(formatter: (String)-> String) =
    run(formatter)