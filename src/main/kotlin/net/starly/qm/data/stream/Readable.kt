package net.starly.qm.data.stream

import org.bukkit.util.io.BukkitObjectInputStream

interface Readable {

    fun read(buf: BukkitObjectInputStream)

}